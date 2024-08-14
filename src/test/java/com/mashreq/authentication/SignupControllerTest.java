package com.mashreq.authentication;

import com.mashreq.AbstractControllerTest;
import com.mashreq.users.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Need to use @Isolated annotation as we need to verify mailService usage as this is mocked in
 * AbstractControllerTest class.
 *
 * <p>This to avoid resetting of the mock in parallel unit tests where one unit test can reset
 * mocked mailService while other one still using it.
 */
@Isolated
class SignupControllerTest extends AbstractControllerTest {

  @Test
  void testSignup_validPayload_shouldSucceedAndReturn201() throws Exception {
    // GIVEN
    String email = generateEmail();
    var payload = buildPayload(email);

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isCreated());

    // AND
    User user = userRepository.findOneByEmail(email).get();
    Assertions.assertEquals(email, user.getEmail());
    Assertions.assertEquals("Test", user.getFirstName());
    Assertions.assertEquals("User", user.getLastName());
    Assertions.assertNotNull(user.getPassword());
    // Ensure that password got encrypted
    Assertions.assertNotEquals("Super_secret1", user.getPassword());
    Assertions.assertNotNull(user.getCreatedOn());
    Assertions.assertNotNull(user.getModifiedOn());
  }

  @Test
  void testSignup_blankPayload_shouldFailAndReturn400() throws Exception {
    // WHEN
    var result = whenCallEndpoint_signUp(null);

    // THEN
    result.andExpect(status().isUnprocessableEntity());
  }

  @Test
  void testSignup_withExistingEmail_shouldFailAndReturn409() throws Exception {
    // GIVEN
    var user = givenUser();
    var payload = buildPayload(user.getEmail());

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isConflict());
    result.andExpect(content().string("Email address already exists"));
  }

  @Test
  void testSignup_invalidEmail_shouldReturnBadRequest() throws Exception {
    // GIVEN
    var payload = buildPayload("notemail");

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.email", is("Must be valid Email")));
  }

  @Test
  void testSignup_withInvalidPassword_shouldFail() throws Exception {
    // GIVEN
    var email = generateEmail();
    var wrongPassword = "wrong_pass";
    var payload = buildPayload(email, wrongPassword, wrongPassword);

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(
        jsonPath(
            "$.password",
            is("Password must be between 8 and 24 characters and should contain at least 1 "
                + "capital letter, 1 lowercase letter, 1 number and 1 special character")));
  }

  @Test
  void testSignup_emptyPassword_shouldFail() throws Exception {
    // GIVEN
    var payload = buildPayload("test@email.com", "", null);

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.password", is("Password is required")));
  }

  @Test
  void testSignup_withPasswordDontMatchConfirm_shouldFail() throws Exception {
    // GIVEN
    var payload = buildPayload("test@email.com", "Str0ng_pass", "d0ntMatch!");

    // WHEN
    var result = whenCallEndpoint_signUp(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.password", is("Passwords do not match")));
  }

  private ResultActions whenCallEndpoint_signUp(String payload)
      throws Exception {
    return callPost(SIGNUP_URL, payload);
  }

  private String buildPayload(String email) {
    return buildPayload(email, null, null);
  }

  private String buildPayload(String email, String password, String confirmPassword) {
    var defaultPassword = "Super_secret1";
    return """    
        {
           "email": "%s",
           "first_name": "Test",
           "last_name": "User",
           "password": "%s",
           "confirm_password": "%s"
           }
        """.formatted(email,
        Optional.ofNullable(password).orElse(defaultPassword),
        Optional.ofNullable(confirmPassword).orElse(defaultPassword));
  }
}
