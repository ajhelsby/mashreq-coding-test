package com.mashreq.rooms.results;

import com.mashreq.bookings.results.AvailableRange;
import com.mashreq.bookings.results.BookingSummaryResult;
import com.mashreq.rooms.Room;
import java.util.List;


public record RoomResult(
    Room room,
    List<BookingSummaryResult> bookingsToday,
    // Todo available slot could also be returned
    List<AvailableRange> availableRangeToday
) {
}