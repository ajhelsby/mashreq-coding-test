package com.mashreq.rooms;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for Room entity.
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {

  Room findByName(String roomName);

  @Query("SELECT MAX(r.capacity) FROM Room r")
  Integer findMaxCapacity();

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
              AND b.status != 'CANCELLED'
          )
          AND NOT EXISTS (
              SELECT 1
              FROM recurring_bookings rb
              WHERE rb.room_id = r.id
              AND rb.start_time < (:endTimeLocal)::TIME  -- Convert endTime to TIME
              AND rb.end_time > (:startTimeLocal)::TIME  -- Convert startTime to TIME
              AND rb.status != 'CANCELLED'
          )
          ORDER BY r.capacity ASC
          LIMIT 1
      """, nativeQuery = true)
  Optional<Room> findBestAvailableRoom(
      LocalDateTime startTime,
      LocalDateTime endTime,
      LocalTime startTimeLocal,
      LocalTime endTimeLocal,
      int numberOfPeople
  );

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
              AND b.status != 'CANCELLED'
          )
          AND NOT EXISTS (
              SELECT 1
              FROM recurring_bookings rb
              WHERE rb.room_id = r.id
              AND rb.start_time < (:endTimeLocal)::TIME  -- Convert endTime to TIME
              AND rb.end_time > (:startTimeLocal)::TIME  -- Convert startTime to TIME
              AND rb.status != 'CANCELLED'
          )
          ORDER BY r.capacity ASC
      """, nativeQuery = true)
  List<Room> findAvailableRooms(
      LocalDateTime startTime,
      LocalDateTime endTime,
      LocalTime startTimeLocal,
      LocalTime endTimeLocal,
      int numberOfPeople);
}
