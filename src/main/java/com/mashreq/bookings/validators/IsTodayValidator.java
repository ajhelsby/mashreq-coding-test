package com.mashreq.bookings.validators;

import com.mashreq.bookings.validators.IsToday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class IsTodayValidator implements ConstraintValidator<IsToday, Instant> {

  @Override
  public boolean isValid(Instant instant, ConstraintValidatorContext context) {
    if (instant == null) {
      return true; // consider using @NotNull for mandatory validation
    }

    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

    return zonedDateTime.toLocalDate().equals(now.toLocalDate());
  }
}
