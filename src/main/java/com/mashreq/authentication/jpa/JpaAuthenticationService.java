package com.mashreq.authentication.jpa;

import com.mashreq.authentication.AbstractAuthenticationService;
import com.mashreq.authentication.payloads.SignupPayload;
import com.mashreq.common.exceptions.EmailTakenException;
import com.mashreq.users.User;
import com.mashreq.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public JpaAuthenticationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    super(userRepository);
    log.info("JpaAuthenticationService Created");

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void signup(SignupPayload payload) {

    if (userRepository.findOneByEmail(payload.email()).isPresent()) {
      throw new EmailTakenException();
    }

    User userToRegister = payload.toEntity();
    userToRegister.setPassword(passwordEncoder.encode(payload.password()));

    User created = userRepository.save(userToRegister);
  }

}
