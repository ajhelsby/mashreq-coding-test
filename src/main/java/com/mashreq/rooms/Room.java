package com.mashreq.rooms;

import com.mashreq.common.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation of the Room entity in the system.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room extends BaseEntity {
  private String name;

  private String location;

  private int capacity;
}
