package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.common.I18n;
import com.mashreq.common.TimeUtils;
import com.mashreq.common.exceptions.AuthenticationException;
import com.mashreq.common.exceptions.BookingFailedException;
import com.mashreq.common.exceptions.MaintenanceInProgressException;
import com.mashreq.common.exceptions.NoRoomsAvailableException;
import com.mashreq.common.exceptions.ResourceNotFoundException;
import com.mashreq.rooms.Room;
import com.mashreq.rooms.RoomService;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import com.mashreq.users.UserService;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling booking operations.
 */
@Slf4j
@Service
public class BookingService {

  private static final int MAX_RETRIES = 3;

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
   * @throws NoRoomsAvailableException if no rooms are available for
   *                                   the requested time and capacity
   */
  @Transactional
  public BookingResult createBooking(AuthenticatedUser authenticatedUser, BookingRequest payload)
      throws NoRoomsAvailableException {
    int retryCount = 0;

    while (retryCount < MAX_RETRIES) {
      try {
        User user = userService.getUserByUsername(authenticatedUser.getUsername());
        Room room = roomService.getAvailableRoomWithOptimumCapacity(
            payload.startTime(), payload.endTime(), payload.numberOfPeople());

        Booking booking = BookingRequest.toBooking(payload, user, room);
        bookingRepository.save(booking);

        return BookingResult.toResult(booking);

      } catch (OptimisticLockException e) {
        retryCount++;
        if (retryCount >= MAX_RETRIES) {
          log.error("Failed to create booking after " + MAX_RETRIES + " attempts", e);
          throw new BookingFailedException(
              payload.startTime(), payload.endTime(), payload.numberOfPeople());
        }
      } catch (NoRoomsAvailableException e) {
        log.warn(
            "No available rooms found for the given time and capacity. Checking recurring bookings."
        );
        checkRecurringMaintenanceBooking(
            payload.startTime(), payload.endTime(), payload.numberOfPeople());
        throw e; // Re-throw to signal failure
      }
    }
    throw new BookingFailedException(
        payload.startTime(), payload.endTime(), payload.numberOfPeople());
  }

  /**
   * Checks for recurring maintenance bookings that might overlap with the given time range.
   *
   * @param startTime      the start time of the booking request
   * @param endTime        the end time of the booking request
   * @param numberOfPeople the number of people for the booking
   * @throws MaintenanceInProgressException if there is an ongoing maintenance booking
   *                                        that overlaps with the request
   */
  private void checkRecurringMaintenanceBooking(
      LocalDateTime startTime, LocalDateTime endTime, int numberOfPeople)
      throws MaintenanceInProgressException {
    log.info("Checking for recurring maintenance bookings for the given time range...");

    // Convert LocalDateTime to LocalTime using system default time zone
    LocalTime startLocalTime = TimeUtils.convertLocalDateTimeToLocalTime(startTime);
    LocalTime endLocalTime = TimeUtils.convertLocalDateTimeToLocalTime(endTime);

    List<RecurringBooking> recurringBookings =
        recurringBookingRepository.findRecurringMaintenanceBooking(
            startLocalTime, endLocalTime, numberOfPeople
        );

    if (!recurringBookings.isEmpty()) {
      // If recurring maintenance found, throw an exception
      throw new MaintenanceInProgressException();
    }
  }

  /**
   * Cancels a booking if the authenticated user is authorized to do so.
   *
   * @param authenticatedUser the user requesting the cancellation
   * @param bookingId         the ID of the booking to be cancelled
   * @throws ResourceNotFoundException if the booking does not exist
   * @throws AuthenticationException   if the authenticated user is not
   *                                   authorized to cancel the booking
   */
  @Transactional
  public void cancelBooking(AuthenticatedUser authenticatedUser, UUID bookingId) {
    // Fetch the booking from the repository
    Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

    // Check if the booking exists
    Booking booking = bookingOptional.orElseThrow(() -> {
      log.error("Booking with ID {} not found.", bookingId);
      return new ResourceNotFoundException(
          I18n.getMessage("error.booking.notFound", bookingId));
    });

    // Retrieve the user making the request
    User user = userService.getUserByUsername(authenticatedUser.getUsername());

    // Check if the authenticated user is the owner of the booking
    if (!booking.getUser().equals(user)) {
      log.warn(
          "User {} is not authorized to cancel booking with ID {}.", user.getEmail(), bookingId);
      throw new AuthenticationException(
          I18n.getMessage("error.booking.unauthorized", bookingId));
    }

    // Update the status of the booking
    booking.setStatus(BookingStatus.CANCELLED);
    bookingRepository.save(booking);

    // Log the successful cancellation
    log.info("Booking with ID {} successfully cancelled by user {}.", bookingId, user.getEmail());
  }
}
