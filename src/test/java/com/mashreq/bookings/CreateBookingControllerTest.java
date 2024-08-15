package com.mashreq.bookings;

import com.mashreq.AbstractControllerTest;
import com.mashreq.rooms.Room;
import com.mashreq.rooms.RoomRepository;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateBookingControllerTest extends AbstractControllerTest {

  @Autowired
  protected BookingRepository bookingRepository;
  @Autowired
  protected RoomRepository roomRepository;

  @ParameterizedTest
  @MethodSource("com.mashreq.bookings.TestBookingUtils#personsAndRoom")
  @CleanBookingTable
  void testCreateBooking_validPayload_shouldSucceed(int numberOfPeople, String roomName)
      throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantAt8Am = createInstantToday(8, 0);
    Instant instantAt9Am = createInstantToday(9, 0);

    var payload = givenPayload(instantAt8Am, instantAt9Am, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.room.name").value(roomName));
  }

  @ParameterizedTest
  @MethodSource("com.mashreq.bookings.TestBookingUtils#personsAndRoomExistingBooking")
  @CleanBookingTable
  void testCreateBooking_bookingTaken_shouldSucceedAndPickOptimumRoom(
      int numberOfPeople, String bookedRoomName, String bestRoomName) throws Exception {

    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant startTime = createInstantToday(8, 0);
    Instant endTime = createInstantToday(9, 0);
    givenBooking(user, bookedRoomName, numberOfPeople, startTime, endTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.room.name").value(bestRoomName));
  }

  @Test
  void testCreateBooking_allBookingTaken_shouldFailWithErrorMessage() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    var numberOfPeople = 20;
    Instant startTime = createInstantToday(8, 0);
    Instant endTime = createInstantToday(9, 0);
    givenBooking(user, "Strive", 15, startTime, endTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isNotFound());
    result.andExpect(content().string("No rooms avaiable between 2024-08-15T04:00:00Z and 2024-08-15T05:00:00Z for 20 people".formatted(startTime, endTime, numberOfPeople)));
  }

  @Test
  void testCreateBooking_allBookingTakenCrossingTime_shouldFailWithErrorMessage() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    var numberOfPeople = 18;
    Instant existingStartTime = createInstantToday(8, 15);
    Instant existingEndTime = createInstantToday(9, 0);
    Instant startTime = createInstantToday(8, 30);
    Instant endTime = createInstantToday(9, 0);
    givenBooking(user, "Strive", 15, existingStartTime, existingEndTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isNotFound());
    result.andExpect(content().string("No rooms avaiable between 2024-08-15T04:00:00Z and 2024-08-15T05:00:00Z for 20 people".formatted(startTime, endTime, numberOfPeople)));
  }

  @Test
  void testCreateBooking_noPayload_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, null);

    // THEN
    result.andExpect(status().isUnprocessableEntity());
  }

  @Test
  void testCreateBooking_noAuth_shouldFail() throws Exception {
    // GIVEN
    Instant instantAt8Am = createInstantToday(8, 0);
    Instant instantAt9Am = createInstantToday(9, 0);
    var payload = givenPayload(instantAt8Am, instantAt9Am, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(null, payload);

    // THEN
    result.andExpect(status().isUnauthorized());
  }

  @Test
  void testCreateBooking_startDateBeforeEndDate_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantAt8Am = createInstantToday(14, 0);
    Instant instantAt9Am = createInstantToday(13, 30);
    var payload = givenPayload(instantAt8Am, instantAt9Am, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.bookingRequest").value("Invalid time range"));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10, 15, 20, 25, 50})
  void testCreateBooking_differentDay_shouldFail(int daysInFuture) throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantStartTomorrow = createInstantOnDay(daysInFuture, 14, 0);
    Instant instantEndTomorrow = createInstantOnDay(daysInFuture, 14, 30);
    var payload = givenPayload(instantStartTomorrow, instantEndTomorrow, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.startTime").value("Start time booking time must be today"));
    result.andExpect(jsonPath("$.endTime").value("End time booking time must be today"));
  }

  @Test
  void testCreateBooking_endsDifferentDay_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantStart = createInstantToday(23, 30);
    Instant instantEndTomorrow = createInstantOnDay(1, 1, 0);
    var payload = givenPayload(instantStart, instantEndTomorrow, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.endTime").value("End time booking time must be today"));
  }

  @Test
  void testCreateBooking_non15MinStartDate_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantAt8Am = createInstantToday(8, 3);
    Instant instantAt9Am = createInstantToday(9, 0);
    var payload = givenPayload(instantAt8Am, instantAt9Am, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.startTime").value("The start time must be a multiple of 15 minutes"));
  }

  @Test
  void testCreateBooking_non15MinEndDate_shouldFail() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantAt8Am = createInstantToday(8, 0);
    Instant instantAt9Am = createInstantToday(9, 17);
    var payload = givenPayload(instantAt8Am, instantAt9Am, 2);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.endTime").value("The end time must be a multiple of 15 minutes"));
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -5, -1, 0, 1, 21, 30, 50, Integer.MAX_VALUE})
  void testCreateBooking_inValidNoOfPeople_shouldFail(int numberOfPeople) throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    Instant instantAt8Am = createInstantToday(8, 0);
    Instant instantAt9Am = createInstantToday(9, 0);
    var payload = givenPayload(instantAt8Am, instantAt9Am, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.numberOfPeople").value("Number of people must be between 2 and the maximum room capacity"));
  }

  private ResultActions whenCallEndpoint_createBooking(AuthenticatedUser principal, String payload)
      throws Exception {
    return callPost(principal, BOOKINGS_URL, payload);
  }

  private String givenPayload(Instant startTime, Instant endTime, int numberOfPeople) {
    return """
        {
          "startTime":"%s",
          "endTime":"%s",
          "numberOfPeople": %s
        }
        """.formatted(startTime.toString(), endTime.toString(), String.valueOf(numberOfPeople));
  }

  private Booking givenBooking(
      User user, String roomName, int noOfPeople, Instant startTime, Instant endTime) {
    Room room = roomRepository.findByName(roomName);
    Booking booking = Booking.builder()
                             .startTime(startTime)
                             .endTime(endTime)
                             .user(user)
                             .room(room)
                             .numberOfPeople(noOfPeople)
                             .build();
    return bookingRepository.save(booking);
  }

  private Instant createInstantToday(int hour, int min) {
    return LocalDate.now()
                    .atTime(hour, min)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
  }

  private Instant createInstantOnDay(int addDays, int hour, int min) {
    return LocalDate.now()
                    .plusDays(addDays)
                    .atTime(hour, min)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

  }
}
