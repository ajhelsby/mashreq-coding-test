package com.mashreq.users.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for validating passwords.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordFieldsValueMatchValidator.class)
@Documented
public @interface PasswordValueMatch {

  /**
   * Get an error message.
   *
   * @return an error message
   */
  String message() default "Fields values don't match!";

  /**
   * Get a group.
   *
   * @return a group
   */
  Class<?>[] groups() default {};

  /**
   * Get a payload.
   *
   * @return a payload
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Get a field.
   *
   * @return a field
   */
  String field();

  /**
   * Get a field match.
   *
   * @return a filed match
   */
  String fieldMatch();

  /**
   * A list of passwords.
   */
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    PasswordValueMatch[] value();
  }
}
