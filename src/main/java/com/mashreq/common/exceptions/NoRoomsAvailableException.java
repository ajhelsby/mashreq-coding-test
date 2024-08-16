package com.mashreq.common.exceptions;

import com.mashreq.common.I18n;
import java.time.LocalDateTime;

/**
 * An exception to be thrown when no rooms are available to be booked.
 */
public class NoRoomsAvailableException extends RuntimeException {

  public NoRoomsAvailableException(
      LocalDateTime startTime, LocalDateTime endTime, int numberOfPeople) {
    super(I18n.getMessage(
        "error.rooms.notAvailable",
        startTime.toString(),
        endTime.toString(),
        numberOfPeople
    ));
  }
}
