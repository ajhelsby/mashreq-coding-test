package com.mashreq.rooms;

import com.mashreq.rooms.results.RoomResult;
import java.util.List;
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
}
