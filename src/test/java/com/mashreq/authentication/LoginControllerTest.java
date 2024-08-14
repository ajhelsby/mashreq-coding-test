package com.mashreq.authentication;

import com.mashreq.AbstractControllerTest;
import com.mashreq.users.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a test to validate all permutations of login
 */
@Isolated
class LoginControllerTest extends AbstractControllerTest {

  @BeforeEach
  void beforeEach() {
    clearDB();
  }

  @Test
  void testLogin_validPayload_shouldSucceedAndReturn200() throws Exception {
    // GIVEN
    var email = generateEmail();
    var password = "Super_secret1";
    var user = givenUser(email, "First", "Last", password);
    var payload = buildPayload(email, password);

    // WHEN
    var result = whenCallEndpoint_login(payload);

    // THEN
    result.andExpect(status().isOk());

    // AND
    result.andExpect(jsonPath("$.user.email").value(user.getEmail()));
    result.andExpect(jsonPath("$.user.firstName").value(user.getFirstName()));
    result.andExpect(jsonPath("$.user.lastName").value(user.getLastName()));
    result.andExpect(jsonPath("$.tokens.token").isString());
    result.andExpect(jsonPath("$.tokens.refreshToken").isString());
  }

  @Test
  void testLogin_blankPayload_shouldFailAndReturn400() throws Exception {
    // GIVEN
    String email = generateEmail();
    String password = "Super_secret1";
    givenUser(email, "First", "Last", password);
    // WHEN
    var result = whenCallEndpoint_login(null);

    // THEN
    result.andExpect(status().isUnprocessableEntity());
  }

  @Test
  void testLogin_withNoEmail_shouldFailAndReturn400() throws Exception {
    // GIVEN
    var email = generateEmail();
    var password = "Super_secret1";
    givenUser(email, "First", "Last", password);
    var payload = buildPayload(null, password);

    // WHEN
    var result = whenCallEndpoint_login(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.email").value("Must be valid Email"));;
  }

  @Test
  void testLogin_invalidEmail_shouldReturnBadRequest() throws Exception {
    // GIVEN
    var email = generateEmail();
    var password = "Super_secret1";
    givenUser(email, "First", "Last", password);
    var payload = buildPayload("notemail", password);

    // WHEN
    var result = whenCallEndpoint_login(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.email", is("Must be valid Email")));
  }

  @Test
  void testLogin_withInvalidPassword_shouldFail() throws Exception {
    // GIVEN
    var email = generateEmail();
    var password = "Super_secret1";
    givenUser(email, "First", "Last", password);
    var wrongPassword = "wrong_pass";
    var payload = buildPayload(email, wrongPassword);

    // WHEN
    var result = whenCallEndpoint_login(payload);

    // THEN
    result.andExpect(status().isUnauthorized());
    result.andExpect(content().string("Bad credentials"));
  }

  @Test
  void testLogin_emptyPassword_shouldFail() throws Exception {
    // GIVEN
    var email = generateEmail();
    var password = "Super_secret1";
    givenUser(email, "First", "Last", password);
    var payload = buildPayload("test@email.com", "");

    // WHEN
    var result = whenCallEndpoint_login(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.password", is("Password is required")));
  }

  private ResultActions whenCallEndpoint_login(String payload)
      throws Exception {
    return callPost(LOGIN_URL, payload);
  }

  private String buildPayload(String email) {
    return buildPayload(email, null);
  }

  private String buildPayload(String email, String password) {
    var defaultPassword = "Super_secret1";
    return """    
        {
           "email": "%s",
           "password": "%s"
           }
        """.formatted(email,
        Optional.ofNullable(password).orElse(defaultPassword));
  }
}
