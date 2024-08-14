package com.mashreq.bookings.payload;

import java.time.Instant;

public record BookingRequest(
    Instant startTime,
    Instant endTime,
    int numberOfPeople
) {
}
