package com.mashreq.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * This is invoked when user tries to access a secured REST resource without supplying any
 * credentials. We should just send a 401 Unauthorized response because there is no 'login page' to
 * redirect to.
 */
@Slf4j
@Component
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    // Here you can place any message you want
    log.info("Handling AuthenticationEntryPoint error {}", authException.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
  }
}
