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
   * Retrieves a list of all rooms.
   *
   * @return a list of RoomResult objects representing all rooms
   */
  public List<RoomResult> getRooms() {
    log.debug("Fetching all rooms from the repository.");
    List<Room> rooms = roomRepository.findAll();
    List<RoomResult> roomResults = rooms.stream()
                                        .map(RoomResult::new)
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
}
