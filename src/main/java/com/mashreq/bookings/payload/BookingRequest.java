package com.mashreq.bookings.payload;

import com.mashreq.bookings.Booking;
import com.mashreq.bookings.validators.IsMultipleOf15Minutes;
import com.mashreq.bookings.validators.IsToday;
import com.mashreq.bookings.validators.ValidNumberOfPeople;
import com.mashreq.bookings.validators.ValidTimeRange;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    int numberOfPeople,

    @Size(max = 100, message = "{error.booking.nameLength}")
    @NotNull(message = "{error.booking.nameNull}")
    String name,


    @Size(max = 255, message = "{error.booking.descriptionLength}")
    @NotNull(message = "{error.booking.descriptionNull}")
    String description
) {
  public static Booking toBooking(BookingRequest payload, User user, Room room) {
    Booking booking = Booking.builder()
                             .startTime(payload.startTime)
                             .endTime(payload.endTime)
                             .numberOfPeople(payload.numberOfPeople)
                             .build();
    booking.setRoom(room);
    booking.setUser(user);
    return booking;
  }
}
