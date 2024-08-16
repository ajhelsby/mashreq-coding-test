package com.mashreq.bookings.results;

import com.mashreq.bookings.Booking;
import com.mashreq.bookings.BookingType;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import java.time.LocalDateTime;

public record BookingSummaryResult(
    String name,
    String description,
    LocalDateTime startTime,
    LocalDateTime endTime,
    User user,
    int numberOfPeople,
    BookingType bookingType
) {
  public static BookingSummaryResult toResult(Booking booking) {
    // Extract fields from the Booking object
    LocalDateTime startTime = booking.getStartTime();
    LocalDateTime endTime = booking.getEndTime();
    User user = booking.getUser();
    Room room = booking.getRoom();
    int numberOfPeople = booking.getNumberOfPeople();

    // Create and return a new BookingResult instance
    return new BookingSummaryResult(booking.getName(), booking.getDescription(), startTime, endTime, user, numberOfPeople, BookingType.MEETING);
  }
}