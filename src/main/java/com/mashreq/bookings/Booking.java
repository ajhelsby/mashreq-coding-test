package com.mashreq.bookings;

import com.mashreq.common.models.BaseEntity;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation of the Booking entity in the system.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking extends BaseEntity {

  @ManyToOne
  private User user;

  @ManyToOne
  private Room room;

  @Column(name = "number_of_people", nullable = false)
  private int numberOfPeople;

  @Column(name = "start_time", nullable = false)
  private Instant startTime;

  @Column(name = "end_time", nullable = false)
  private Instant endTime;

  @Enumerated(EnumType.STRING)
  private BookingStatus status = BookingStatus.BOOKED;
}
