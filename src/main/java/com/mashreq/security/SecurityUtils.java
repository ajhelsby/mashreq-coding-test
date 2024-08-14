package com.mashreq.security;

import com.mashreq.security.jwt.TokenService;
import com.mashreq.users.User;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper class to get user from security context.
 */
@Slf4j
public class SecurityUtils {

  // JWT Token related
  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final int TOKEN_PREFIX_LENGTH = 7;
  public static final String LOGIN_PATH = "/api/v1/auth/login";

  private SecurityUtils() {
    throw new IllegalStateException("SecurityUtility class");
  }

  /**
   * A good user is a user whose account status is Active.
   *
   * @param user a user to check
   * @return true if user is active
   */
  public static boolean isUserValid(User user) {
    // This could be used to check a user status is active
    return true;
  }

  /**
   * Helper to get the authenticated user from security context.
   *
   * @return a AuthenticatedUser or null if it does not exist in context
   */
  public static AuthenticatedUser getCurrentUser() {
    return getCurrentUser(SecurityContextHolder.getContext());
  }

  /**
   * Helper to get the authenticated user from security context.
   *
   * @return a AuthenticatedUser or null if it does not exist in context
   */
  public static AuthenticatedUser getCurrentUser(SecurityContext context) {
    if (context != null) {
      Authentication auth = context.getAuthentication();
      if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser authUser) {
        return authUser;
      }
    }
    return null;
  }

}
