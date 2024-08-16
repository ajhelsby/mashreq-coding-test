package com.mashreq.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * This is invoked when user tries to access a secured REST resource without the necessary
 * authorization. We should just send a 403 Forbidden response because there is no 'error' page to
 * redirect to.
 */
@Slf4j
@Component
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    log.info("Handling SimpleAccessDeniedHandler error {}", accessDeniedException.getMessage());
    response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
  }
}
