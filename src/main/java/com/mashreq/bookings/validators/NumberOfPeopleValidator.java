package com.mashreq.bookings.validators;

import org.springframework.beans.factory.annotation.Autowired;
import com.mashreq.rooms.RoomService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberOfPeopleValidator implements ConstraintValidator<ValidNumberOfPeople, Integer> {

  @Autowired
  private RoomService roomService; // Inject your service to access the room data

  @Override
  public boolean isValid(Integer numberOfPeople, ConstraintValidatorContext context) {
    // Fetch the maximum room capacity
    int maxCapacity = roomService.getMaxCapacity();

    // Validate
    return numberOfPeople >= 2 && numberOfPeople <= maxCapacity;
  }
}
