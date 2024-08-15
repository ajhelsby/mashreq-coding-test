package com.mashreq.bookings;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class TestBookingUtils {
  private static Stream<Arguments> personsAndRoom() {
    return Stream.of(
        Arguments.of(2, "Amaze"),
        Arguments.of(3, "Amaze"),
        Arguments.of(4, "Beauty"),
        Arguments.of(5, "Beauty"),
        Arguments.of(6, "Beauty"),
        Arguments.of(7, "Beauty"),
        Arguments.of(8, "Inspire"),
        Arguments.of(9, "Inspire"),
        Arguments.of(10, "Inspire"),
        Arguments.of(11, "Inspire"),
        Arguments.of(12, "Inspire"),
        Arguments.of(13, "Strive"),
        Arguments.of(14, "Strive"),
        Arguments.of(15, "Strive"),
        Arguments.of(16, "Strive"),
        Arguments.of(17, "Strive"),
        Arguments.of(18, "Strive"),
        Arguments.of(19, "Strive"),
        Arguments.of(20, "Strive")
    );
  }

  private static Stream<Arguments> personsAndRoomExistingBooking() {
    return Stream.of(
        Arguments.of(2, "Amaze", "Beauty"),
        Arguments.of(3, "Amaze", "Beauty"),
        Arguments.of(4, "Beauty", "Inspire"),
        Arguments.of(5, "Beauty", "Inspire"),
        Arguments.of(6, "Beauty", "Inspire"),
        Arguments.of(7, "Beauty", "Inspire"),
        Arguments.of(8, "Inspire", "Strive"),
        Arguments.of(9, "Inspire", "Strive"),
        Arguments.of(10, "Inspire", "Strive"),
        Arguments.of(11, "Inspire", "Strive"),
        Arguments.of(12, "Inspire", "Strive")
    );
  }

  private static Stream<Arguments> maintenanceBookingCheck() {
    return Stream.of(
        Arguments.of(8, 45, 9, 30),
        Arguments.of(9, 0, 9, 30),
        Arguments.of(9, 0, 9, 15),
        Arguments.of(12, 45, 13, 30),
        Arguments.of(13, 0, 13, 30),
        Arguments.of(13, 0, 13, 15),
        Arguments.of(16, 45, 17, 30),
        Arguments.of(17, 0, 17, 30),
        Arguments.of(17, 0, 17, 15)
    );
  }
}
