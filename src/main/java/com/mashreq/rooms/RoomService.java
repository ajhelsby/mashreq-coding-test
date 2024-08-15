package com.mashreq.rooms;

import com.mashreq.common.exceptions.NoRoomsAvailableException;
import com.mashreq.rooms.results.RoomResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
  private RoomRepository roomRepository;

  public RoomService(RoomRepository roomRepository) {
    this.roomRepository = roomRepository;
  }

  public List<RoomResult> getRooms() {
    var rooms = roomRepository.findAll();
    return rooms.stream().map(RoomResult::new).collect(Collectors.toList());
  }

  public Room getAvailableRoomWithOptimumCapacity(
      Instant startTime, Instant endTime, int numberOfPeople) {

    Optional<Room> roomOptional = roomRepository.findBestAvailableRoom(startTime, endTime, numberOfPeople);

    if (roomOptional.isEmpty()) {
      throw new NoRoomsAvailableException(startTime, endTime, numberOfPeople);
    }

    return roomOptional.get();
  }
}
