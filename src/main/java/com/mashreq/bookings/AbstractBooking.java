package com.mashreq.bookings;

import com.mashreq.common.models.BaseEntity;
import com.mashreq.rooms.Room;
import com.mashreq.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractBooking extends BaseEntity {
  @Version
  private Long version;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @Column
  private String name;

  @Column
  private String description;

  @Enumerated(EnumType.STRING)
  @Column
  private BookingStatus status = BookingStatus.BOOKED;
}
