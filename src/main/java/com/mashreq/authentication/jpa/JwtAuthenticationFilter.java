package com.mashreq.authentication.jpa;

import com.mashreq.common.I18n;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.security.SecurityUtils;
import com.mashreq.security.jwt.JwsService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Main authentication filter for the app to protect our API.
 *
 * <p>OncePerRequestFilter filter to make sure single execution per request dispatch.
 *
 * <p>It performs checks for the authorization header and authenticate the JWT token and sets
 * authentication in security context.
 *
 * <p>This filter only handles tokens with "auth" audience.
 *
 * <p>NOTE: Don't initialise this class as a spring bean, as it will be added to default security
 * filter chain and will be executed for all the uri.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final JwsService jwsService;

  public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwsService jwsService) {
    this.userDetailsService = userDetailsService;
    this.jwsService = jwsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    log.debug("Inside JWTAuthenticationFilter ...");

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    // we have token in header
    if (StringUtils.hasText(header) && header.startsWith(SecurityUtils.TOKEN_PREFIX)) {

      log.debug("Found a token");
      String token = header.substring(SecurityUtils.TOKEN_PREFIX_LENGTH);

      try {

        Authentication auth = createAuthToken(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.debug("Token authentication successful");

      } catch (Exception e) {
        log.error("Token authentication failed {}", e.getMessage());
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            I18n.getMessage("error.user.authentication.failed"));
        return;
      }

    } else {
      log.debug("Token authentication skipped for url {}", request.getRequestURI());
    }

    filterChain.doFilter(request, response);
  }

  protected Authentication createAuthToken(String token) {

    JWTClaimsSet claims = jwsService.parseToken(token, JwsService.AUTH_AUDIENCE);

    String username = claims.getSubject();
    AuthenticatedUser principal =
        (AuthenticatedUser) userDetailsService.loadUserByUsername(username);

    return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
  }
}
