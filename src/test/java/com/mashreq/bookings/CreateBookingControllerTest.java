package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.common.exceptions.BookingFailedException;
import com.mashreq.users.UserService;
import com.mashreq.rooms.RoomService;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Isolated
public class CreateBookingControllerTest extends AbstractBookingControllerTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private UserService userService;

  @Mock
  private RoomService roomService;

  @InjectMocks
  private BookingService bookingService;

  @ParameterizedTest
  @MethodSource("com.mashreq.bookings.TestBookingUtils#personsAndRoom")
  @CleanBookingTable
  void testCreateBooking_validPayload_shouldSucceed(int numberOfPeople, String roomName)
      throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);

    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isCreated());
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
    LocalDateTime startTime = createLocalDateTimeToday(8, 0);
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    givenBooking(user, bookedRoomName, numberOfPeople, startTime, endTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.room.name").value(bestRoomName));
  }

  @Test
  void testCreateBooking_allBookingTaken_shouldFailWithErrorMessage() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    var numberOfPeople = 20;
    LocalDateTime startTime = createLocalDateTimeToday(8, 0);
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    givenBooking(user, "Strive", 15, startTime, endTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isNotFound());
    result.andExpect(content().string("No rooms available between %s and %s for %s people".formatted(startTime, endTime, numberOfPeople)));
  }

  @ParameterizedTest
  @MethodSource("com.mashreq.bookings.TestBookingUtils#maintenanceBookingCheck")
  @CleanBookingTable
  void testCreateBooking_duringMaintenance_shouldFailWithErrorMessage(
      int startHour, int startMin, int endHour, int endMin) throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    var numberOfPeople = 19;
    LocalDateTime startTime = createLocalDateTimeToday(startHour, startMin);
    LocalDateTime endTime = createLocalDateTimeToday(endHour, endMin);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(content().string("No rooms available due to maintenance"));
  }

  @Test
  void testCreateBooking_allBookingTakenCrossingTime_shouldFailWithErrorMessage() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    var numberOfPeople = 18;
    LocalDateTime existingStartTime = createLocalDateTimeToday(8, 15);
    LocalDateTime existingEndTime = createLocalDateTimeToday(9, 0);
    LocalDateTime startTime = createLocalDateTimeToday(8, 30);
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    givenBooking(user, "Strive", 15, existingStartTime, existingEndTime);
    var payload = givenPayload(startTime, endTime, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isNotFound());
    result.andExpect(content().string("No rooms available between %s and %s for %s people".formatted(startTime, endTime, numberOfPeople)));
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
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 2);

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
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(14, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(13, 30);
    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 2);

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
    LocalDateTime LocalDateTimeStartTomorrow = createLocalDateTimeOnDay(daysInFuture, 14, 0);
    LocalDateTime LocalDateTimeEndTomorrow = createLocalDateTimeOnDay(daysInFuture, 14, 30);
    var payload = givenPayload(LocalDateTimeStartTomorrow, LocalDateTimeEndTomorrow, 2);

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
    LocalDateTime LocalDateTimeStart = createLocalDateTimeToday(23, 30);
    LocalDateTime LocalDateTimeEndTomorrow = createLocalDateTimeOnDay(1, 1, 0);
    var payload = givenPayload(LocalDateTimeStart, LocalDateTimeEndTomorrow, 2);

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
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 3);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 2);

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
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 17);
    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 2);

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
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var payload = givenPayload(LocalDateTimeAt8Am, LocalDateTimeAt9Am, numberOfPeople);

    // WHEN
    var result = whenCallEndpoint_createBooking(authUser, payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.numberOfPeople").value("Number of people must be between 2 and the maximum room capacity"));
  }

  @Test
  void testCreateBooking_withRetrySuccess() {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);
    var booking = givenBooking(user, "Strive", 19, LocalDateTimeAt8Am, LocalDateTimeAt9Am);
    var payload = new BookingRequest(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 19, "Name", "Description");
    var room = roomRepository.findByName("Strive");

    // WHEN
    when(userService.getUserByUsername(anyString())).thenReturn(user);
    when(roomService.getAvailableRoomWithOptimumCapacity(any(), any(), anyInt())).thenReturn(room);
    when(bookingRepository.save(any(Booking.class)))
        .thenThrow(new OptimisticLockException())
        .thenThrow(new OptimisticLockException())
        .thenReturn(booking);

    // Act
    BookingResult result = bookingService.createBooking(authUser, payload);

    // Assert
    assertNotNull(result);
    verify(bookingRepository, times(3)).save(any(Booking.class));
  }

  @Test
  void testCreateBooking_withRetryFailure() {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime LocalDateTimeAt8Am = createLocalDateTimeToday(8, 0);
    LocalDateTime LocalDateTimeAt9Am = createLocalDateTimeToday(9, 0);

    var payload = new BookingRequest(LocalDateTimeAt8Am, LocalDateTimeAt9Am, 19, "Name", "Description");
    var room = roomRepository.findByName("Strive");
    // WHEN
    when(userService.getUserByUsername(anyString())).thenReturn(user);
    when(roomService.getAvailableRoomWithOptimumCapacity(any(), any(), anyInt())).thenReturn(room);
    when(bookingRepository.save(any(Booking.class))).thenThrow(new OptimisticLockException());

    // THEN
    assertThrows(BookingFailedException.class, () -> bookingService.createBooking(authUser, payload));
    verify(bookingRepository, times(3)).save(any(Booking.class));
  }
}
