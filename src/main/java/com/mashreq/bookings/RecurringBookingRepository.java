package com.mashreq.bookings;

import com.mashreq.rooms.Room;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecurringBookingRepository extends JpaRepository<RecurringBooking, UUID> {

  @Query(value = """
          SELECT rb.* 
          FROM recurring_bookings rb
          INNER JOIN rooms r ON r.id = rb.room_id
          WHERE rb.booking_type = 'MAINTENANCE'
          AND r.capacity >= :numberOfPeople
          AND rb.start_time < :endTime
          AND rb.end_time > :startTime
          AND rb.status != 'CANCELLED'
      """, nativeQuery = true)
  List<RecurringBooking> findRecurringMaintenanceBooking(
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("numberOfPeople") int numberOfPeople);

  @Query("select r from RecurringBooking r where r.room.id in ?1")
  List<RecurringBooking> findByRoom_IdIn(Collection<UUID> ids);
}
