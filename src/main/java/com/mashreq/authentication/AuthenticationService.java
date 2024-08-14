package com.mashreq.authentication;

import com.mashreq.authentication.payloads.SignupPayload;
import com.mashreq.authentication.results.LoginResult;
import com.mashreq.security.AuthenticatedUser;
import java.util.Map;

public interface AuthenticationService {

  /**
   * Creates new user in the system.
   *
   * @param payload a user details to add
   */
  void signup(SignupPayload payload);

  /**
   * Authenticate user and generate JWT token.
   *
   * @param username the email address in this case
   * @param password user password
   * @return a user with a token
   */
  LoginResult login(String username, String password);

  /**
   * Generate access token and refresh token for a user.
   *
   * @param currentUser    a current user
   */
  Map<String, String> generateTokens(AuthenticatedUser currentUser);
}
