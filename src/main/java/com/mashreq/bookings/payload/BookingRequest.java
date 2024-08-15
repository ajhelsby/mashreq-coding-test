package com.mashreq.bookings.payload;

import com.mashreq.bookings.validators.IsMultipleOf15Minutes;
import com.mashreq.bookings.validators.IsToday;
import com.mashreq.bookings.validators.ValidNumberOfPeople;
import com.mashreq.bookings.validators.ValidTimeRange;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

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

    @ValidNumberOfPeople
    @NotNull
    int numberOfPeople
) {

}
