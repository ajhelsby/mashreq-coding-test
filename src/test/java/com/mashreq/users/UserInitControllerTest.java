package com.mashreq.users;

import com.mashreq.AbstractControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolation required for no users in DB.
 */
@Isolated
class UserInitControllerTest extends AbstractControllerTest {

  @BeforeEach
  void beforeEach() {
    clearDB();
  }

  @Test
  void testInitRootUsers_validPayload_shouldSucceed() throws Exception {

    // GIVEN
    var email1 = generateEmail();
    var email2 = generateEmail();
    var payload = givenPayload(email1, email2);

    // WHEN
    var result = whenCallEndpoint_initUser(payload);
    result.andExpect(status().isCreated());

    // THEN
    var user1 = userRepository.findOneByEmail(email1).get();
    Assertions.assertEquals(email1, user1.getEmail());
    Assertions.assertEquals("User", user1.getFirstName());
    Assertions.assertEquals("One", user1.getLastName());
    Assertions.assertNotNull(user1.getPassword());
    Assertions.assertNotNull(user1.getCreatedOn());
    Assertions.assertNotNull(user1.getModifiedOn());
    Assertions.assertNull(user1.getLastLoggedInOn());

    var user2 = userRepository.findOneByEmail(email2).get();
    Assertions.assertEquals(email2, user2.getEmail());
    Assertions.assertEquals("User", user2.getFirstName());
    Assertions.assertEquals("Two", user2.getLastName());
    Assertions.assertNotNull(user2.getPassword());
    Assertions.assertNotNull(user2.getCreatedOn());
    Assertions.assertNotNull(user2.getModifiedOn());
    Assertions.assertNull(user2.getLastLoggedInOn());
  }

  @Test
  void testInitRootUsers_whenUsersExist_thenShouldFail() throws Exception {

    // GIVEN
    givenUser();
    var email1 = generateEmail();
    var email2 = generateEmail();
    var payload = givenPayload(email1, email2);

    // WHEN
    var result = whenCallEndpoint_initUser(payload);
    result.andExpect(status().isConflict());
    result.andExpect(content().string("Operation is not allowed"));

    // THEN
    Assertions.assertTrue(userRepository.findOneByEmail(email1).isEmpty());
    Assertions.assertTrue(userRepository.findOneByEmail(email2).isEmpty());
  }

  @Test
  void testInitRootUsers_emptyPayload_shouldFail() throws Exception {
    // GIVEN
    var payload = """
        {}
        """;

    // WHEN
    var result = whenCallEndpoint_initUser(payload);
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.users").value("Field is required"));
  }

  @Test
  void testInitRootUsers_invalidPayload_shouldFail() throws Exception {
    // GIVEN
    var payload = """
        {
           "users" :
               [
                  {
                       "email": "wrong",
                       "first_name": "User",
                       "last_name": "One",
                       "password": "wrong"
                   }
               ]
        }
        """;

    // WHEN
    var result = whenCallEndpoint_initUser(payload);

    // THEN
    result.andExpect(status().isBadRequest());
    result.andExpect(content().json("""
        {
           "users[0].email" : "Must be valid Email",
           "users[0].password" : "Password must be between 8 and 24 characters and should contain at least 1 capital letter, 1 lowercase letter, 1 number and 1 special character"
                                                                        
        }
        """));
  }

  private ResultActions whenCallEndpoint_initUser(String payload)
      throws Exception {
    return callPost(USERS_INIT_URL, payload);
  }

  private String givenPayload(String... emails) {
    return """   
                
                {
           "users" :
               [
                    {
                        "email": "%s",
                        "first_name": "User",
                        "last_name": "One",
                        "password": "Super_secret1"
                    },
                    {
                       "email": "%s",
                       "first_name": "User",
                       "last_name": "Two",
                       "password": "Super_secret1"
                  }
               ]
        }
        """.formatted(emails);
  }
}
