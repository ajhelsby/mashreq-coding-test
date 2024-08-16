package com.mashreq.common;

import java.sql.Time;
import java.time.LocalDate;
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

  /**
   * Converts the given LocalTime to a LocalDateTime for today's date.
   *
   * @param time the LocalTime to be combined with today's date
   * @return a LocalDateTime object representing today's date and the given time
   */
  public static LocalDateTime convertToLocalDateTimeForToday(Time time) {
    // Get today's date
    LocalDate today = LocalDate.now();

    // Combine today's date with the provided LocalTime
    return LocalDateTime.of(today, time.toLocalTime());
  }
}
