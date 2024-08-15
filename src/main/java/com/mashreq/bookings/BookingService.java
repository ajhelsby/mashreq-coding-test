package com.mashreq.bookings;

import com.mashreq.bookings.payload.BookingRequest;
import com.mashreq.bookings.results.BookingResult;
import com.mashreq.rooms.Room;
import com.mashreq.rooms.RoomService;
import com.mashreq.security.AuthenticatedUser;
import com.mashreq.users.User;
import com.mashreq.users.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private BookingRepository bookingRepository;
  private RoomService roomService;
  private UserService userService;

  public BookingService(
      BookingRepository bookingRepository, RoomService roomService, UserService userService) {
    this.bookingRepository = bookingRepository;
    this.roomService = roomService;
    this.userService = userService;
  }

  @Transactional
  public BookingResult createBooking(AuthenticatedUser authenticatedUser, BookingRequest payload) {
    User user = userService.getUserByUsername(authenticatedUser.getUsername());
    // Get the available rooms in the timeframe with enough capacity
    Room room = roomService.getAvailableRoomWithOptimumCapacity(payload.startTime(), payload.endTime(), payload.numberOfPeople());

    Booking booking = Booking.builder()
                             .room(room)
                             .user(user)
                             .startTime(payload.startTime())
                             .endTime(payload.endTime())
                             .numberOfPeople(payload.numberOfPeople())
                             .build();
    bookingRepository.save(booking);
    return BookingResult.toResult(booking);
  }
}
