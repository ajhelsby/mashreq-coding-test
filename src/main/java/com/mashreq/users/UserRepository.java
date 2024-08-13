package com.mashreq.users;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findOneByEmail(String email);

  Optional<User> findOneByExternalId(String externalId);
}
