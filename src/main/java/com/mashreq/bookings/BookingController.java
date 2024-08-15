package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.common.BaseApiV1Controller;
import com.mashreq.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController extends BaseApiV1Controller {

  private BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping(path = "bookings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingResult> create(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @Valid @RequestBody BookingRequest payload
  ) {
    return ResponseEntity.ok(bookingService.createBooking(authenticatedUser, payload));
  }
}
