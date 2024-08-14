package com.mashreq.rooms;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Room entity.
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {

}
