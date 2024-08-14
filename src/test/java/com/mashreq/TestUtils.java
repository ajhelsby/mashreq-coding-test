package com.mashreq;

import com.mashreq.users.User;
import java.util.UUID;
import org.springframework.util.StringUtils;

/**
 * Utility class for unit tests
 *
 * @author P Maksymchuk
 */
public final class TestUtils {

  private static int modelSequenceNumber = 0;

  private static int getModelSequenceNumber() {
    return modelSequenceNumber++;
  }

  public static User buildUser() {
    return buildUser(generateEmail(), "Test " + getModelSequenceNumber(), "User", null);
  }

  public static User buildUser(
      String email,
      String firstName,
      String lastName,
      String password) {
    return User.builder()
        .email(email)
        .firstName(StringUtils.hasText(firstName) ? firstName : "User " + getModelSequenceNumber())
        .lastName(StringUtils.hasText(firstName) ? lastName : "User")
        .password(password)
        .build();
  }

  public static String generateEmail() {
    return UUID.randomUUID() + "@mashreq.com";
  }
}
