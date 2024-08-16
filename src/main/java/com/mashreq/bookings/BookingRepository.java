package com.mashreq.bookings;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

  @Query("""
      select b
      from Booking b
      where b.room.id in ?1 
      and DATE(b.startTime) = ?2 
      and b.status = 'BOOKED'
      """)
  List<Booking> findByRoom_IdInAndToday(
      Collection<UUID> ids, LocalDate today);
}
