package com.mashreq.rooms;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for Room entity.
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {
  @Query(value = """
    SELECT r.* 
    FROM rooms r
    WHERE r.capacity >= :numberOfPeople
    AND NOT EXISTS (
        SELECT 1
        FROM bookings b
        WHERE b.room_id = r.id
        AND b.start_time < :endTime
        AND b.end_time > :startTime
    )
    AND NOT EXISTS (
        SELECT 1
        FROM recurring_bookings rb
        WHERE rb.room_id = r.id
        AND rb.start_time < (:endTimeLocal)::TIME  -- Convert endTime to TIME
        AND rb.end_time > (:startTimeLocal)::TIME  -- Convert startTime to TIME
    )
    ORDER BY r.capacity ASC
    LIMIT 1
""", nativeQuery = true)
  Optional<Room> findBestAvailableRoom(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("startTimeLocal") LocalTime startTimeLocal,
      @Param("endTimeLocal") LocalTime endTimeLocal,
      @Param("numberOfPeople") int numberOfPeople
  );

  Room findByName(String roomName);

  @Query("SELECT MAX(r.capacity) FROM Room r")
  Integer findMaxCapacity();
}
