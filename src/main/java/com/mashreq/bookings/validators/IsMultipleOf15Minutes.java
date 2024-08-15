package com.mashreq.bookings.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IsMultipleOf15MinutesValidator.class)
@Target({ElementType.PARAMETER, FIELD })
@Retention(RUNTIME)
public @interface IsMultipleOf15Minutes {
  String message() default "{error.time.15mins}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
