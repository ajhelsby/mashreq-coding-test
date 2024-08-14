package com.mashreq.authentication;

import com.mashreq.common.I18n;
import com.mashreq.common.exceptions.ResourceNotFoundException;
import com.mashreq.users.User;
import com.mashreq.users.UserRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for authentication service.
 */
@Slf4j
public abstract class AbstractAuthenticationService implements AuthenticationService {

  private final UserRepository userRepository;

  protected AbstractAuthenticationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  protected User getUser(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException(I18n.getMessage("error.user.notFound")));
  }

  protected User getUserByUsername(String username) {
    return userRepository
        .findOneByEmail(username)
        .orElseThrow(
            () -> new ResourceNotFoundException(I18n.getMessage("error.user.notFound")));
  }
}
