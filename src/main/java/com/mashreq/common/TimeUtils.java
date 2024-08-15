package com.mashreq.common;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.LocalDateTime;

public final class TimeUtils {
  /**
   * Converts an {@link LocalDateTime} to a {@link LocalTime} using the system default time zone.
   *
   * @param LocalDateTime the {@link LocalDateTime} to convert
   * @return the corresponding {@link LocalTime}
   */
  public static LocalTime convertLocalDateTimeToLocalTime(LocalDateTime LocalDateTime) {
    return LocalDateTime.atZone(ZoneId.systemDefault()).toLocalTime();
  }
}
