package com.mashreq.bookings.results;

import com.mashreq.bookings.BookingType;
import com.mashreq.bookings.Booking;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import java.time.LocalDateTime;

public record BookingResult(
    String name,
    String description,
    LocalDateTime startTime,
    LocalDateTime endTime,
    User user,
    Room room,
    int numberOfPeople,
    BookingType bookingType
) {
  public static BookingResult toResult(Booking booking) {
    // Extract fields from the Booking object
    LocalDateTime startTime = booking.getStartTime();
    LocalDateTime endTime = booking.getEndTime();
    User user = booking.getUser();
    Room room = booking.getRoom();
    int numberOfPeople = booking.getNumberOfPeople();

    // Create and return a new BookingResult instance
    return new BookingResult(booking.getName(), booking.getDescription(), startTime, endTime, user, room, numberOfPeople, BookingType.MEETING);
  }
}
