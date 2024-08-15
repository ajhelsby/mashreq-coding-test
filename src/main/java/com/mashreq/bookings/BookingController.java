package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for managing bookings.
 */
@RestController
public class BookingController extends BaseApiV1Controller {

  private final BookingService bookingService;

  /**
   * Constructs a BookingController with the given BookingService.
   *
   * @param bookingService the service to handle booking operations
   */
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  /**
   * Creates a new booking.
   *
   * @param authenticatedUser the authenticated user making the request
   * @param payload           the booking request payload
   * @return the created booking result
   */
  @PostMapping(
      path = "/bookings",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(summary = "Create a new booking", description = "Creates a new booking with the given details.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created the booking"),
      @ApiResponse(responseCode = "400", description = "Invalid input provided")
  })
  public ResponseEntity<BookingResult> create(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @Valid @RequestBody BookingRequest payload
  ) {
    BookingResult result = bookingService.createBooking(authenticatedUser, payload);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * Cancels an existing booking.
   *
   * @param authenticatedUser the authenticated user making the request
   * @param id                the ID of the booking to cancel
   * @return a response indicating the booking was successfully canceled
   */
  @DeleteMapping(path = "/bookings/{id}")
  @Operation(summary = "Cancel a booking", description = "Cancels an existing booking identified by the ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully canceled the booking"),
      @ApiResponse(responseCode = "404", description = "Booking not found")
  })
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @PathVariable("id") @Parameter(description = "The ID of the booking to cancel") UUID id
  ) {
    bookingService.cancelBooking(authenticatedUser, id);
    return ResponseEntity.noContent().build();
  }
}
