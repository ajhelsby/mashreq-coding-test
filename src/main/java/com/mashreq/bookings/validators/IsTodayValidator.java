package com.mashreq.bookings.validators;

import com.mashreq.bookings.validators.IsToday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class IsTodayValidator implements ConstraintValidator<IsToday, LocalDateTime> {

  @Override
  public boolean isValid(LocalDateTime LocalDateTime, ConstraintValidatorContext context) {
    if (LocalDateTime == null) {
      return true; // consider using @NotNull for mandatory validation
    }

    ZonedDateTime zonedDateTime = LocalDateTime.atZone(ZoneId.systemDefault());
    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

    return zonedDateTime.toLocalDate().equals(now.toLocalDate());
  }
}
