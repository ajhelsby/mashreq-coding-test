package com.mashreq.bookings.results;

import com.mashreq.bookings.Booking;


public record BookingResult(

) {
  public static BookingResult toResult(Booking booking) {
    return new BookingResult();
  }
}
