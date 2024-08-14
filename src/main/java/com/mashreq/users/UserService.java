package com.mashreq.users;

import com.mashreq.common.I18n;
import com.mashreq.users.payloads.UserListAddPayload;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Slf4j
@Service
public class UserService {

  protected final UserRepository userRepository;
  protected final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void initUsers(UserListAddPayload payload) {
    log.info("Starting init() users...");

    // Only initialise on empty DB
    if (userRepository.count() > 0) {
      throw new AccessDeniedException(I18n.getMessage("error.operation.notAllowed"));
    }

    payload.users().forEach(
        u -> {
          log.info("Creating the root admin user: {}", u.email());
          if (userRepository.findOneByEmail(u.email()).isPresent()) {
            log.error("Email already taken for root user {}, will ignore", u.email());
          }

          var newUser = u.toEntity();
          newUser.setPassword(passwordEncoder.encode(u.password()));
          newUser.setCreatedOn(Instant.now());
          newUser.setModifiedOn(Instant.now());

          userRepository.save(newUser);
        });

    log.info("Done init() users");
  }
}
