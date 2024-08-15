package com.mashreq.bookings.results;

import com.mashreq.bookings.Booking;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import java.time.Instant;

public record BookingResult(
    Instant startTime,
    Instant endTime,
    User user,
    Room room,
    int numberOfPeople
) {
  public static BookingResult toResult(Booking booking) {
    // Extract fields from the Booking object
    Instant startTime = booking.getStartTime();
    Instant endTime = booking.getEndTime();
    User user = booking.getUser();
    Room room = booking.getRoom();
    int numberOfPeople = booking.getNumberOfPeople();

    // Create and return a new BookingResult instance
    return new BookingResult(startTime, endTime, user, room, numberOfPeople);
  }
}
