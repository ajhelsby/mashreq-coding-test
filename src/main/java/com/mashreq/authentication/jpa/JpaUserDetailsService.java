package com.mashreq.authentication.jpa;

import com.mashreq.security.AuthenticatedUser;
import com.mashreq.security.BaseUserDetailsService;
import com.mashreq.users.User;
import com.mashreq.users.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * JPA implementation of UserDetailsService, as required by Spring Security.
 */
@Slf4j
@Service
@Qualifier("jpaUserDetailsService")
public class JpaUserDetailsService extends BaseUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public JpaUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Finds a user by the given username.
   *
   * @param username a username to find user by
   * @return a User
   */
  public Optional<User> findUser(String username) {
    return userRepository.findOneByEmail(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading user with username: " + username);

    User user =
        findUser(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    UserDetails userDetails = new AuthenticatedUser(user);
    // this allows to check user status (locked, enabled) after token been generated
    new AccountStatusUserDetailsChecker().check(userDetails);
    log.debug("Loaded user with username: {}", username);

    return userDetails;
  }
}
