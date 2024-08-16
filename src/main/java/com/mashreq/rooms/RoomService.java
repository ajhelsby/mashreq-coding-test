package com.mashreq.rooms;

import com.mashreq.bookings.Booking;
import com.mashreq.bookings.BookingRepository;
import com.mashreq.bookings.RecurringBooking;
import com.mashreq.bookings.RecurringBookingRepository;
import com.mashreq.bookings.results.BookingSummaryResult;
import com.mashreq.common.TimeUtils;
import com.mashreq.common.exceptions.NoRoomsAvailableException;
import com.mashreq.rooms.results.RoomResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing rooms, including retrieving available rooms and their capacities.
 */
@Slf4j
@Service
public class RoomService {

  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;
  private final RecurringBookingRepository recurringBookingRepository;

  /**
   * Constructs a new RoomService instance.
   *
   * @param roomRepository             the repository for accessing room data
   * @param bookingRepository          the repository for accessing booking data
   * @param recurringBookingRepository the repository for accessing recurring booking data
   */
  public RoomService(
      RoomRepository roomRepository,
      BookingRepository bookingRepository,
      RecurringBookingRepository recurringBookingRepository) {
    this.roomRepository = roomRepository;
    this.bookingRepository = bookingRepository;
    this.recurringBookingRepository = recurringBookingRepository;
  }

  /**
   * Retrieves a list of rooms based on the provided time window.
   * If no time window is provided, all rooms are retrieved.
   *
   * @param startTime      the start time of the range to check availability (can be null)
   * @param endTime        the end time of the range to check availability (can be null)
   * @param numberOfPeople optional number of people the room search would be used for
   * @return a list of RoomResult objects representing rooms either within the time range or
   * all rooms
   * @throws IllegalArgumentException if endTime is provided without startTime or vice versa
   */
  public List<RoomResult> getRooms(
      LocalDateTime startTime, LocalDateTime endTime,
      Integer numberOfPeople) {

    // Validate the input parameters
    List<Room> rooms;
    boolean hasTimeParams = validateTimeParameters(startTime, endTime);
    int numOfPeople = numberOfPeople == null ? 0 : numberOfPeople.intValue();

    if (hasTimeParams) {
      log.debug("Fetching available rooms for time range from {} to {}.", startTime, endTime);
      LocalTime startTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(startTime);
      LocalTime endTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(endTime);
      rooms =
          roomRepository.findAvailableRooms(
              startTime,
              endTime,
              startTimeLocal,
              endTimeLocal,
              numOfPeople
          );

    } else {
      log.debug("Fetching all rooms from the repository.");
      rooms = roomRepository.findAll();
    }
    List<UUID> roomIds = rooms.parallelStream().map(Room::getId).toList();

    LocalDate today = LocalDate.now();

    // Fetch bookings and recurring bookings separately
    List<Booking> bookings = bookingRepository.findByRoom_IdInAndToday(roomIds, today);
    List<RecurringBooking> recurringBookings = recurringBookingRepository.findByRoom_IdIn(roomIds);

    // Combine bookings and recurring bookings into a single map by room ID
    Map<UUID, List<BookingSummaryResult>> bookingSummaryByRoom =
        Stream.concat(
                  bookings.stream().map(BookingSummaryResult::toResult),
                  recurringBookings.stream().map(BookingSummaryResult::toResult)
              )
              .collect(Collectors.groupingBy(BookingSummaryResult::roomId));

    // Convert rooms to RoomResult
    List<RoomResult> roomResults =
        rooms.stream()
             .map(room -> {
               List<BookingSummaryResult> bookingSummaries =
                   bookingSummaryByRoom.getOrDefault(room.getId(), List.of());
               bookingSummaries.sort(Comparator.comparing(BookingSummaryResult::startTime));
               return new RoomResult(room, bookingSummaries, null);
             })
             .collect(Collectors.toList());

    log.debug("Retrieved {} rooms.", roomResults.size());
    return roomResults;
  }

  /**
   * Finds an available room with the optimum capacity within the specified time range.
   *
   * @param startTime      the start time of the booking
   * @param endTime        the end time of the booking
   * @param numberOfPeople the number of people requiring the room
   * @return the available room with the optimum capacity
   * @throws NoRoomsAvailableException if no suitable room is found
   */
  public Room getAvailableRoomWithOptimumCapacity(
      LocalDateTime startTime, LocalDateTime endTime, int numberOfPeople) {

    log.debug(
        "Searching for available rooms from {} to {} for {} people.",
        startTime,
        endTime,
        numberOfPeople);

    LocalTime startTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(startTime);
    LocalTime endTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(endTime);

    Optional<Room> roomOptional =
        roomRepository.findBestAvailableRoom(
            startTime,
            endTime,
            startTimeLocal,
            endTimeLocal,
            numberOfPeople
        );

    if (roomOptional.isEmpty()) {
      log.warn(
          "No available room found for the given time range: {} to {} with capacity for {} people.",
          startTime,
          endTime,
          numberOfPeople
      );
      throw new NoRoomsAvailableException(startTime, endTime, numberOfPeople);
    }

    Room room = roomOptional.get();
    log.info(
        "Found available room with ID {} for the time range: {} to {}.",
        room.getId(),
        startTime,
        endTime
    );
    return room;
  }

  /**
   * Retrieves the maximum room capacity from the repository.
   *
   * @return the maximum capacity of all rooms
   */
  public int getMaxCapacity() {
    log.debug("Fetching maximum room capacity from the repository.");
    int maxCapacity = roomRepository.findMaxCapacity();
    log.debug("Maximum room capacity is {}.", maxCapacity);
    return maxCapacity;
  }

  private boolean validateTimeParameters(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime != null && endTime == null) {
      throw new IllegalArgumentException("endTime must be provided if startTime is provided");
    }
    if (endTime != null && startTime == null) {
      throw new IllegalArgumentException("startTime must be provided if endTime is provided");
    }

    return startTime != null && endTime != null;
  }
}
