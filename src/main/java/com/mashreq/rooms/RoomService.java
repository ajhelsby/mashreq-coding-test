package com.mashreq.rooms;

import com.mashreq.common.exceptions.NoRoomsAvailableException;
import com.mashreq.common.TimeUtils;
import com.mashreq.rooms.results.RoomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing rooms, including retrieving available rooms and their capacities.
 */
@Slf4j
@Service
public class RoomService {

  private final RoomRepository roomRepository;

  /**
   * Constructs a RoomService with the given RoomRepository.
   *
   * @param roomRepository the repository for accessing room data
   */
  public RoomService(RoomRepository roomRepository) {
    this.roomRepository = roomRepository;
  }

  /**
   * Retrieves a list of rooms based on the provided time window.
   * If no time window is provided, all rooms are retrieved.
   *
   * @param startTime      the start time of the range to check availability (can be null)
   * @param endTime        the end time of the range to check availability (can be null)
   * @param numberOfPeople
   * @return a list of RoomResult objects representing rooms either within the time range or all rooms
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
      rooms = roomRepository.findAvailableRooms(startTime, endTime, startTimeLocal, endTimeLocal, numOfPeople);

    } else {
      log.debug("Fetching all rooms from the repository.");
      rooms = roomRepository.findAll();
    }

    // Convert rooms to RoomResult
    List<RoomResult> roomResults = rooms.stream()
                                        .map(room -> new RoomResult(room, null, null))
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

    log.debug("Searching for available rooms from {} to {} for {} people.", startTime, endTime, numberOfPeople);
    LocalTime startTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(startTime);
    LocalTime endTimeLocal = TimeUtils.convertLocalDateTimeToLocalTime(endTime);

    Optional<Room> roomOptional = roomRepository.findBestAvailableRoom(startTime, endTime, startTimeLocal, endTimeLocal, numberOfPeople);

    if (roomOptional.isEmpty()) {
      log.warn("No available room found for the given time range: {} to {} with capacity for {} people.", startTime, endTime, numberOfPeople);
      throw new NoRoomsAvailableException(startTime, endTime, numberOfPeople);
    }

    Room room = roomOptional.get();
    log.info("Found available room with ID {} for the time range: {} to {}.", room.getId(), startTime, endTime);
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
