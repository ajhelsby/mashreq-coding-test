package com.mashreq.bookings.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class IsMultipleOf15MinutesValidator implements ConstraintValidator<IsMultipleOf15Minutes, LocalDateTime> {

  @Override
  public boolean isValid(LocalDateTime LocalDateTime, ConstraintValidatorContext context) {
    if (LocalDateTime == null) {
      return true; // consider using @NotNull for mandatory validation
    }

    ZonedDateTime zonedDateTime = LocalDateTime.atZone(ZoneId.systemDefault());
    int minutes = zonedDateTime.getMinute();

    return minutes % 15 == 0;
  }
}
