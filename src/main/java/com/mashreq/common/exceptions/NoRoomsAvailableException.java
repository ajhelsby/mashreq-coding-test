package com.mashreq.common.exceptions;

import com.mashreq.common.I18n;
import java.time.Instant;

/**
 * An exception to be thrown when no rooms are available to be booked.
 */
public class NoRoomsAvailableException extends RuntimeException {

  public NoRoomsAvailableException(Instant startTime, Instant endTime, int numberOfPeople) {
    super(I18n.getMessage("error.rooms.notAvailable", startTime.toString(), endTime.toString(), numberOfPeople));
  }
}
