package com.mashreq.authentication;

import com.mashreq.authentication.payloads.LoginPayload;
import com.mashreq.authentication.payloads.SignupPayload;
import com.mashreq.authentication.results.LoginResult;
import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.common.exceptions.AuthenticationException;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API endpoints to handle authentication related functionalities.
 */
@RestController
@Slf4j
public class AuthController extends BaseApiV1Controller {

  private final AuthenticationService authenticationService;

  public AuthController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  /**
   * This endpoint is used for registering a user on the platform. I would normally
   * include some level of user verification to ensure a successful sign up, but this
   * will be skipped for this application
   */
  @PostMapping(value = "auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
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

  /**
   * Handle user login. Once user successfully authenticated generate JWT token
   * and return with user details.
   *
   * @param loginPayload a login credentials
   * @return a token with user details
   * @throws AuthenticationException when supplied credentials invalid
   */
  @PostMapping(value = "auth/login", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Allow a user to login to the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created booking"),
      @ApiResponse(responseCode = "500", description = "Internal error"),
      @ApiResponse(responseCode = "422", description = "When failed form validation"),
      @ApiResponse(responseCode = "401", description = "When bad credentials provided.")
  })
  public ResponseEntity<LoginResult> login(@Valid @RequestBody final LoginPayload loginPayload)
      throws AuthenticationException {

    // return the token and basic user details with token
    return ResponseEntity
        .ok(authenticationService.login(loginPayload.email(), loginPayload.password()));
  }

}
