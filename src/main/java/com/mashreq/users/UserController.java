package com.mashreq.users;

import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.users.payloads.UserListAddPayload;
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


  @PostMapping(value = "users/init", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> initUsers(@RequestBody @Valid UserListAddPayload payload) {
    userService.initUsers(payload);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
