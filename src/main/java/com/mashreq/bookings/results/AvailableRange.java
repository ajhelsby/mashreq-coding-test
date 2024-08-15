package com.mashreq.bookings.results;

import java.time.LocalTime;

public record AvailableRange(
    LocalTime startTime,
    LocalTime endTime
) {
}
