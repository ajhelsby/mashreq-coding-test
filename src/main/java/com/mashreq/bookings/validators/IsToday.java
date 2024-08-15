package com.mashreq.bookings.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IsTodayValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface IsToday {
  String message() default "{error.time.today}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}