package com.mashreq.rooms;

import com.mashreq.AbstractControllerTest;
import com.mashreq.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a test to test retrieving room information
 */
public class RoomControllerTest extends AbstractControllerTest {

  @Test
  void testGetRooms_shouldReturn200() throws Exception {
    // GIVEN
    // WHEN
    var result = whenCallEndpoint_rooms(givenAuthUser());
    // THEN
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  void testGetRooms_invalidAuth_shouldReturn401() throws Exception {
    // GIVEN
    // WHEN
    var result = whenCallEndpoint_rooms(null);
    // THEN
    result.andExpect(status().isUnauthorized());
  }

  private ResultActions whenCallEndpoint_rooms(AuthenticatedUser principal)
      throws Exception {
    return callGet(principal, ROOMS_URL);
  }
}
