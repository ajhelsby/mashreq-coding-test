package com.mashreq.bookings.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class IsMultipleOf15MinutesValidator implements ConstraintValidator<IsMultipleOf15Minutes, Instant> {

  @Override
  public boolean isValid(Instant instant, ConstraintValidatorContext context) {
    if (instant == null) {
      return true; // consider using @NotNull for mandatory validation
    }

    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
    int minutes = zonedDateTime.getMinute();

    return minutes % 15 == 0;
  }
}
