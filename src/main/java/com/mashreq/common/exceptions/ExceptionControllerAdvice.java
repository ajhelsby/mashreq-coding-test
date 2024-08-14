package com.mashreq.common.exceptions;

import com.google.common.base.CaseFormat;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global API endpoint exception handler.
 *
 * <p>Any exception thrown in the app should be caught here.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> resourceNotFound(ResourceNotFoundException ex) {
    log.error("ResourceNotFoundException exception: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(EmailTakenException.class)
  public ResponseEntity<String> emailTaken(EmailTakenException ex) {
    log.error("EmailTakenException exception: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Handle an authentication exception and return a unauthorized status code.
   *
   * @param ex a AuthenticationException
   * @return a ResponseEntity with a status code of 401 and body of the exception message
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<String> handleAuthenticationException(final AuthenticationException ex) {
    log.error(ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex)
      throws AccessDeniedException {
    log.error("AccessDeniedException exception: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Handles Validation exception.
   *
   * @param ex a MethodArgumentNotValidException
   * @return a map of field name and error messages
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> validation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        // need snake case field name to be returned to FE
        var fieldName = CaseFormat.LOWER_CAMEL.to(
            CaseFormat.LOWER_UNDERSCORE, ((FieldError) error).getField());
        var errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });
    return errors;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> unprocessedEntity(HttpMessageNotReadableException ex) {
    log.error("HttpMessageNotReadableException exception: {}", ex.getMessage());
    return new ResponseEntity<>("Cannot process incoming request", HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Handles any unknown errors.
   *
   * @param request a HttpServletRequest
   * @param ex      a Throwable
   * @return a ResponseEntity
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> globalException(HttpServletRequest request, Throwable ex) {
    String errorReferenceCode = UUID.randomUUID().toString();
    log.error("Unhandled Exception error [code=" + errorReferenceCode + "]", ex);
    return new ResponseEntity<>(
        "Internal Error, ref " + errorReferenceCode, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
