package com.mashreq.authentication.jpa;

import com.mashreq.authentication.AbstractAuthenticationService;
import com.mashreq.authentication.payloads.SignupPayload;
import com.mashreq.authentication.results.LoginResult;
import com.mashreq.common.I18n;
import com.mashreq.common.exceptions.AuthenticationException;
import com.mashreq.common.exceptions.EmailTakenException;
import com.mashreq.common.exceptions.ResourceNotFoundException;
import com.mashreq.configs.ApplicationProperties;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.security.jwt.JwsService;
import com.mashreq.security.jwt.TokenService;
import com.mashreq.users.User;
import com.mashreq.users.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * JPA implementation of the authentication service. It uses user from the database to handle all
 * the functionality.
 *
 * <p>Configures a JpaAuthenticationService as default when the property <code>modules.auth</code>
 * isn't defined or set to JPA.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "modules.auth", havingValue = "JPA", matchIfMissing = true)
public class JpaAuthenticationService extends AbstractAuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwsService jwsService;
  private final ApplicationProperties appProperties;

  public JpaAuthenticationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwsService jwsService,
      ApplicationProperties appProperties
  ) {
    super(userRepository);
    log.info("JpaAuthenticationService Created");

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwsService = jwsService;
    this.appProperties = appProperties;
  }

  @Override
  @Transactional
  public void signup(SignupPayload payload) {

    if (userRepository.findOneByEmail(payload.email()).isPresent()) {
      throw new EmailTakenException();
    }

    User userToRegister = payload.toEntity();
    userToRegister.setPassword(passwordEncoder.encode(payload.password()));
    userToRegister.setCreatedOn(LocalDateTime.now());
    userToRegister.setModifiedOn(LocalDateTime.now());

    userRepository.save(userToRegister);
  }

  @Override
  public LoginResult login(String username, String password) {
    log.debug("User logging in");

    // Perform the security
    final Authentication auth = authenticate(username, password);
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) auth.getPrincipal();
    User user;
    try {
      user = getUserByUsername(username);
      // Check disabled users password
      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new AuthenticationException(I18n.getMessage("error.auth.badCredentials"));
      }
    } catch (ResourceNotFoundException rnfe) {
      throw new AuthenticationException(I18n.getMessage("error.auth.badCredentials"));
    }

    final Map<String, String> token = generateTokens(authenticatedUser);

    log.debug("Successfully logged in user");

    // update last login date
    user.setLastLoggedInOn(LocalDateTime.now());
    userRepository.save(user);

    return new LoginResult(user, token);
  }

  /**
   * Handle User authentication.
   * If anything wrong with credentials, an {@link AuthenticationException} will be thrown.
   *
   * @param username a username
   * @param password a password
   */
  private Authentication authenticate(final String username, final String password)
      throws AuthenticationException {
    Objects.requireNonNull(username);
    Objects.requireNonNull(password);

    try {
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (BadCredentialsException e) {
      log.warn(e.getMessage(), e);
      throw new AuthenticationException("Bad credentials", e);
    }
  }

  @Override
  public Map<String, String> generateTokens(AuthenticatedUser currentUser) {
    log.debug("Handling authentication");

    // allows to overwrite default expiry time for the token
    long tokenExpiry = appProperties.getJwt().getExpiry().getAuthToken();

    var accessToken =
        jwsService.createToken(JwsService.AUTH_AUDIENCE, currentUser.getUsername(), tokenExpiry);
    var refreshToken =
        jwsService.createToken(
            JwsService.REFRESH_AUTH_AUDIENCE,
            currentUser.getUsername(),
            appProperties.getJwt().getExpiry().getRefreshToken());

    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put("token", accessToken);
    tokenMap.put("refreshToken", refreshToken);

    return tokenMap;
  }

}
