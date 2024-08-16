package com.mashreq.security;

import com.mashreq.users.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * BaseUserDetailsService, as required by Spring Security.
 */
@Slf4j
public abstract class BaseUserDetailsService implements UserDetailsService {


  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    log.debug("Loading user having username: " + username);

    User user =
        findUser(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));


    UserDetails userDetails = new AuthenticatedUser(user);
    // this allows to check user status (locked, enabled) after token been generated
    new AccountStatusUserDetailsChecker().check(userDetails);
    log.debug("Loaded user having username: {}", username);

    return userDetails;
  }

  /**
   * Finds a user by the given username.
   *
   * <p>Override this if you aren't using email as the username or
   * need to search by another field.
   */
  public abstract Optional<User> findUser(String username);
}
