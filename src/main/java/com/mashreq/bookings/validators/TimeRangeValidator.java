package com.mashreq.bookings.validators;

import com.mashreq.bookings.payload.BookingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, BookingRequest> {

  @Override
  public boolean isValid(BookingRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true; // Handle null cases if necessary
    }
    // Check if startTime is before endTime
    return request.startTime().isBefore(request.endTime());
  }
}
