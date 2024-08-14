package com.mashreq.users;

import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.users.payloads.UserListAddPayload;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
public class UserController extends BaseApiV1Controller {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }


  /**
   *
   * @param payload
   * @return
   */
  @PostMapping(value = "users/init", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Users successfully created"),
      @ApiResponse(responseCode = "500", description = "Internal error"),
      @ApiResponse(responseCode = "409", description = "User already exists.")
  })
  public ResponseEntity<Void> initUsers(@RequestBody @Valid UserListAddPayload payload) {
    userService.initUsers(payload);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
