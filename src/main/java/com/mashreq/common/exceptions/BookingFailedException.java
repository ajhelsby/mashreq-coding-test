package com.mashreq.common.exceptions;

import com.mashreq.common.I18n;
import java.time.LocalDateTime;

/**
 * An exception to be thrown when no rooms are available to be booked.
 */
public class BookingFailedException extends RuntimeException {

  public BookingFailedException(
      LocalDateTime startTime, LocalDateTime endTime, int numberOfPeople) {

    super(I18n.getMessage(
        "error.rooms.bookingFailed",
        startTime.toString(),
        endTime.toString(),
        numberOfPeople
    ));
  }
}
