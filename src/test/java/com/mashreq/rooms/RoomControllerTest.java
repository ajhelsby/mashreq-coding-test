package com.mashreq.rooms;

import com.mashreq.AbstractControllerTest;
import com.mashreq.bookings.Booking;
import com.mashreq.bookings.BookingRepository;
import com.mashreq.bookings.CleanBookingTable;
import com.mashreq.bookings.validators.IsMultipleOf15Minutes;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RequestParam;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a test to test retrieving room information
 */
public class RoomControllerTest extends AbstractControllerTest {

  @Autowired
  protected BookingRepository bookingRepository;
  @Autowired
  protected RoomRepository roomRepository;

  @Test
  void testGetRooms_shouldReturn200() throws Exception {
    // GIVEN
    // WHEN
    var result = whenCallEndpoint_rooms(givenAuthUser());
    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  @CleanBookingTable
  void testGetRooms_withDateFilter_shouldReturn200() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime startTime = createLocalDateTimeToday(8, 0);
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    givenBooking(user, "Amaze", 2, startTime, endTime);
    givenBooking(user, "Beauty", 6, startTime, endTime);
    givenBooking(user, "Inspire", 11, startTime, endTime);

    // WHEN
    var result = whenCallEndpoint_roomsWithFilters(authUser, startTime, endTime, null);

    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.length()").value(1));
    result.andExpect(jsonPath("$[0].room.name").value("Strive"));
  }

  @Test
  @CleanBookingTable
  void testGetRooms_withDateFilterAndPeople_shouldReturn200() throws Exception {
    // GIVEN
    var user = givenUser();
    var authUser = givenAuthUser(user);
    LocalDateTime startTime = createLocalDateTimeToday(8, 0);
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    givenBooking(user, "Amaze", 2, startTime, endTime);
    givenBooking(user, "Beauty", 6, startTime, endTime);
    givenBooking(user, "Strive", 19, startTime, endTime);

    // WHEN
    var result = whenCallEndpoint_roomsWithFilters(authUser, startTime, endTime, 11);

    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.length()").value(1));
    result.andExpect(jsonPath("$[0].room.name").value("Inspire"));
  }

  @Test
  void testGetRooms_withInvalidStartTimeFilter_shouldReturn400() throws Exception {
    // GIVEN
    LocalDateTime endTime = createLocalDateTimeToday(9, 0);
    // WHEN
    var result = whenCallEndpoint_roomsWithFilters(givenAuthUser(), null, endTime, null);
    // THEN
    result.andExpect(status().isBadRequest());
  }

  @Test
  void testGetRooms_withInvalidEndTimeFilter_shouldReturn400() throws Exception {
    // GIVEN
    LocalDateTime startTime = createLocalDateTimeToday(8, 0);
    // WHEN
    var result = whenCallEndpoint_roomsWithFilters(givenAuthUser(), startTime, null, null);
    // THEN
    result.andExpect(status().isBadRequest());
  }

  @Test
  void testGetRooms_invalidAuth_shouldReturn401() throws Exception {
    // GIVEN
    // WHEN
    var result = whenCallEndpoint_rooms(null);
    // THEN
    result.andExpect(status().isUnauthorized());
  }

  private ResultActions whenCallEndpoint_rooms(AuthenticatedUser principal)
      throws Exception {
    return callGet(principal, ROOMS_URL);
  }

  private ResultActions whenCallEndpoint_roomsWithFilters(
      AuthenticatedUser principal, LocalDateTime startTime, LocalDateTime endTime, Integer numberOfPeople)
      throws Exception {
    String baseUrl = ROOMS_URL;
    String queryString = Stream.of(
                                   startTime != null ? "startTime=" + startTime : null,
                                   endTime != null ? "endTime=" + endTime : null,
                                   numberOfPeople != null ? "numberOfPeople=" + numberOfPeople : null)
                               .filter(param -> param != null)
                               .collect(Collectors.joining("&"));

    // Construct the final URL with query string if needed
    String url = queryString.isEmpty() ? baseUrl : baseUrl + "?" + queryString;
    return callGet(principal, url);
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

  protected LocalDateTime createLocalDateTimeToday(int hour, int min) {
    return LocalDate.now()
                    .atTime(hour, min)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
  }
}
