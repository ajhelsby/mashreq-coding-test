package com.mashreq.common.exceptions;

import com.mashreq.common.I18n;
import java.time.LocalDateTime;

/**
 * An exception to be thrown when no rooms are available to be booked.
 */
public class MaintenanceInProgressException extends RuntimeException {

  public MaintenanceInProgressException() {
    super(I18n.getMessage("error.rooms.maintenance"));
  }
}
