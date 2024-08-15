package com.mashreq.bookings.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashreq.bookings.validators.IsMultipleOf15Minutes;
import com.mashreq.bookings.validators.IsToday;
import com.mashreq.bookings.validators.ValidTimeRange;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.checkerframework.common.value.qual.IntRange;

/**
 * POJO for creating a Booking.
 */
@ValidTimeRange
public record BookingRequest(
    @NotNull
    @IsToday(message = "{error.startTime.today}")
    @IsMultipleOf15Minutes(message = "{error.startTime.15mins}")
    Instant startTime,

    @NotNull
    @IsToday(message = "{error.endTime.today}")
    @IsMultipleOf15Minutes(message = "{error.endTime.15mins}")
    Instant endTime,

    @NotNull
    int numberOfPeople
) {

}
