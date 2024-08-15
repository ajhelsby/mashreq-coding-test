package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.common.TimeUtils;
import com.mashreq.common.exceptions.MaintenanceInProgressException;
import com.mashreq.common.exceptions.NoRoomsAvailableException;
import com.mashreq.rooms.Room;
import com.mashreq.rooms.RoomService;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import com.mashreq.users.UserService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling booking operations.
 */
@Slf4j
@Service
public class BookingService {

  private final RecurringBookingRepository recurringBookingRepository;
  private final BookingRepository bookingRepository;
  private final RoomService roomService;
  private final UserService userService;

  /**
   * Constructs a BookingService with the required repositories and services.
   *
   * @param recurringBookingRepository the repository for recurring bookings
   * @param bookingRepository          the repository for bookings
   * @param roomService                the service for room operations
   * @param userService                the service for user operations
   */
  public BookingService(
      RecurringBookingRepository recurringBookingRepository,
      BookingRepository bookingRepository,
      RoomService roomService,
      UserService userService) {
    this.recurringBookingRepository = recurringBookingRepository;
    this.bookingRepository = bookingRepository;
    this.roomService = roomService;
    this.userService = userService;
  }

  /**
   * Creates a booking based on the provided request and user.
   *
   * @param authenticatedUser the authenticated user making the booking
   * @param payload           the details of the booking request
   * @return a result representing the outcome of the booking creation
   * @throws NoRoomsAvailableException if no rooms are available for the requested time and capacity
   */
  @Transactional
  public BookingResult createBooking(AuthenticatedUser authenticatedUser, BookingRequest payload)
      throws NoRoomsAvailableException {
    try {
      User user = userService.getUserByUsername(authenticatedUser.getUsername());
      Room room = roomService.getAvailableRoomWithOptimumCapacity(payload.startTime(), payload.endTime(), payload.numberOfPeople());

      Booking booking = BookingRequest.toBooking(payload, user, room);
      bookingRepository.save(booking);

      return BookingResult.toResult(booking);

    } catch (NoRoomsAvailableException e) {
      log.warn("No available rooms found for the given time and capacity. Checking recurring bookings...");
      checkRecurringMaintenanceBooking(payload.startTime(), payload.endTime(), payload.numberOfPeople());
      throw e; // Re-throw to signal failure
    }
  }

  /**
   * Checks for recurring maintenance bookings that might overlap with the given time range.
   *
   * @param startTime      the start time of the booking request
   * @param endTime        the end time of the booking request
   * @param numberOfPeople the number of people for the booking
   * @throws MaintenanceInProgressException if there is an ongoing maintenance booking that overlaps with the request
   */
  private void checkRecurringMaintenanceBooking(
      LocalDateTime startTime, LocalDateTime endTime, int numberOfPeople)
      throws MaintenanceInProgressException {
    log.info("Checking for recurring maintenance bookings for the given time range...");

    // Convert LocalDateTime to LocalTime using system default time zone
    LocalTime startLocalTime = TimeUtils.convertLocalDateTimeToLocalTime(startTime);
    LocalTime endLocalTime = TimeUtils.convertLocalDateTimeToLocalTime(endTime);

    List<RecurringBooking> recurringBookings = recurringBookingRepository.findRecurringMaintenanceBooking(
        startLocalTime, endLocalTime, numberOfPeople
    );

    if (!recurringBookings.isEmpty()) {
      // If recurring maintenance found, throw an exception
      throw new MaintenanceInProgressException();
    }
  }
}
