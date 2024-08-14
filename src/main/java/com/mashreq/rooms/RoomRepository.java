package com.mashreq.rooms;

import java.time.Instant;
import java.util.List;
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
  Room findBestAvailableRoom(
      @Param("startTime") Instant desiredStartTime,
      @Param("endTime") Instant desiredEndTime,
      @Param("numberOfPeople") int numberOfPeople
  );
}
