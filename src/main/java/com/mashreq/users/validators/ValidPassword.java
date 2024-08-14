package com.mashreq.users.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for password constrain, using custom password validator.
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidPassword {

  /**
   * Get error message.
   *
   * @return an error message
   */
  String message() default "Invalid Password";

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
}
