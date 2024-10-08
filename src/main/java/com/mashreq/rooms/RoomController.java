package com.mashreq.rooms;

import com.mashreq.bookings.validators.IsMultipleOf15Minutes;
import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.rooms.results.RoomResult;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RoomController extends BaseApiV1Controller {

  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @GetMapping(path = "rooms", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms"),
      @ApiResponse(responseCode = "500", description = "Internal error"),
      @ApiResponse(responseCode = "401", description = "When bad credentials provided.")
  })
  public ResponseEntity<List<RoomResult>> list(
      @IsMultipleOf15Minutes @RequestParam(value = "startTime", required = false)
      LocalDateTime startTime,
      @IsMultipleOf15Minutes @RequestParam(value = "endTime", required = false)
      LocalDateTime endTime,
      @RequestParam(value = "numberOfPeople", required = false)
      Integer numberOfPeople
  ) {
    return ResponseEntity.ok(roomService.getRooms(startTime, endTime, numberOfPeople));
  }
}
