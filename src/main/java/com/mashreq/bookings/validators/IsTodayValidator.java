package com.mashreq.bookings.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class IsTodayValidator implements ConstraintValidator<IsToday, LocalDateTime> {

  @Override
  public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext context) {
    if (localDateTime == null) {
      return true; // consider using @NotNull for mandatory validation
    }

    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

    return zonedDateTime.toLocalDate().equals(now.toLocalDate());
  }
}
