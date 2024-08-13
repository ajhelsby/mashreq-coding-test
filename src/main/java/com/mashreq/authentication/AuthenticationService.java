package com.mashreq.authentication;

import com.mashreq.authentication.payloads.SignupPayload;

public interface AuthenticationService {

  /**
   * Creates new user in the system and sends verification email.
   *
   * @param payload a user details to add
   */
  void signup(SignupPayload payload);
}
