package com.mashreq.bookings;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Isolated
public class DeleteBookingControllerTest extends AbstractBookingControllerTest {

  @Test
  void testDeleteBooking_validId_shouldSucceed() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime localDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime localDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var booking = givenBooking(user, "Amaze", 2, localDateTimeAt8Am, localDateTimeAt9Am);

    // WHEN
    var result = whenCallEndpoint_deleteBooking(authUser, booking.getId());

    // THEN
    result.andExpect(status().isNoContent());
    var updatedBooking = bookingRepository.findById(booking.getId()).get();
    Assertions.assertEquals(BookingStatus.CANCELLED, updatedBooking.getStatus());
  }

  @Test
  @CleanBookingTable
  void testDeleteBooking_userNotOwner_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime localDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime localDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var anotherUser = givenUser();
    var booking = givenBooking(anotherUser, "Amaze", 2, localDateTimeAt8Am, localDateTimeAt9Am);

    // WHEN
    var result = whenCallEndpoint_deleteBooking(authUser, booking.getId());

    // THEN
    result.andExpect(status().isUnauthorized());
    var updatedBooking = bookingRepository.findById(booking.getId()).get();
    Assertions.assertEquals(BookingStatus.BOOKED, updatedBooking.getStatus());
  }

  @Test
  void testDeleteBooking_doesNotExist_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime localDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime localDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var booking = givenBooking(user, "Amaze", 2, localDateTimeAt8Am, localDateTimeAt9Am);
    // WHEN
    var result = whenCallEndpoint_deleteBooking(authUser, UUID.randomUUID());

    // THEN
    result.andExpect(status().isNotFound());
    var updatedBooking = bookingRepository.findById(booking.getId()).get();
    Assertions.assertEquals(BookingStatus.BOOKED, updatedBooking.getStatus());
  }

  @Test
  void testDeleteBooking_noAuth_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    LocalDateTime localDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime localDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var booking = givenBooking(user, "Amaze", 2, localDateTimeAt8Am, localDateTimeAt9Am);
    // WHEN
    var result = whenCallEndpoint_deleteBooking(null, booking.getId());

    // THEN
    result.andExpect(status().isUnauthorized());
  }

  @Test
  void testDeleteBooking_AndCreateBooking_shouldSucceed() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime localDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime localDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var roomName = "Strive";
    var booking = givenBooking(user, roomName, 20, localDateTimeAt8Am, localDateTimeAt9Am);
    var payload = givenPayload(localDateTimeAt8Am, localDateTimeAt9Am, 20);

    // WHEN
    var result = whenCallEndpoint_deleteBooking(authUser, booking.getId());

    // THEN
    result.andExpect(status().isNoContent());
    var updatedBooking = bookingRepository.findById(booking.getId()).get();
    Assertions.assertEquals(BookingStatus.CANCELLED, updatedBooking.getStatus());

    // WHEN
    var createResult = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    createResult.andExpect(status().isCreated());
    createResult.andExpect(jsonPath("$.room.name").value(roomName));
  }

}
