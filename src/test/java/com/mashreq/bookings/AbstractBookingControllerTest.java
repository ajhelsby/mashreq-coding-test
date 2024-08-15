package com.mashreq.bookings;

import com.mashreq.AbstractControllerTest;
import com.mashreq.rooms.Room;
import com.mashreq.rooms.RoomRepository;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

public class AbstractBookingControllerTest  extends AbstractControllerTest {

  @Autowired
  protected BookingRepository bookingRepository;
  @Autowired
  protected RoomRepository roomRepository;

  protected ResultActions whenCallEndpoint_createBooking(AuthenticatedUser principal, String payload)
      throws Exception {
    return callPost(principal, BOOKINGS_URL, payload);
  }

  protected ResultActions whenCallEndpoint_deleteBooking(AuthenticatedUser principal, UUID bookingId)
      throws Exception {
    var url = String.format("%s/%s", BOOKINGS_URL, bookingId.toString());
    return callDelete(principal, url);
  }

  protected LocalDateTime createLocalDateTimeToday(int hour, int min) {
    return LocalDate.now()
                    .atTime(hour, min)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
  }

  protected LocalDateTime createLocalDateTimeOnDay(int addDays, int hour, int min) {
    return LocalDate.now()
                    .plusDays(addDays)
                    .atTime(hour, min)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

  }

  protected Booking givenBooking(
      User user, String roomName, int noOfPeople, LocalDateTime startTime, LocalDateTime endTime) {
    Room room = roomRepository.findByName(roomName);
    Booking booking = Booking.builder()
                             .startTime(startTime)
                             .endTime(endTime)
                             .numberOfPeople(noOfPeople)
                             .build();
    booking.setUser(user);
    booking.setRoom(room);
    booking.setName("Meeting Name");
    booking.setDescription("Meeting Description");
    return bookingRepository.save(booking);
  }
}
