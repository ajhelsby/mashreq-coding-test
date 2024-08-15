package com.mashreq.rooms;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for Room entity.
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {

  @Query("""
    SELECT r FROM Room r
    WHERE r.capacity >= :numberOfPeople
    AND NOT EXISTS (
        SELECT b FROM Booking b
        WHERE b.room.id = r.id
        AND b.startTime < :endTime
        AND b.endTime > :startTime
    )
    ORDER BY r.capacity ASC
    LIMIT 1
""")
  Optional<Room> findBestAvailableRoom(
      @Param("startTime") Instant startTime,
      @Param("endTime") Instant endTime,
      @Param("numberOfPeople") int numberOfPeople
  );

  Room findByName(String roomName);

  @Query("SELECT MAX(r.capacity) FROM Room r")
  Integer findMaxCapacity();
}
