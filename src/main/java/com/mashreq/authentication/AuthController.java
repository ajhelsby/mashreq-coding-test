package com.mashreq.authentication;

import com.mashreq.authentication.payloads.SignupPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API endpoints to handle authentication related functionalities.
 */
@RestController
@RequestMapping(path = "/api/auth")
@Slf4j
public class AuthController {

  private final AuthenticationService authenticationService;

  public AuthController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  /**
   * This endpoint is used for registering a user on the platform. I would normally
   * include some level of user verification to ensure a successful sign up, but this
   * will be skipped for this application
   */
  @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Register new user in the system.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "successful operation"),
          @ApiResponse(responseCode = "400", description = "Invalid payload"),
          @ApiResponse(responseCode = "409",
              description = "When given email address already taken")
      })
  public ResponseEntity<Void> signup(@Valid @RequestBody SignupPayload payload) {
    authenticationService.signup(payload);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
