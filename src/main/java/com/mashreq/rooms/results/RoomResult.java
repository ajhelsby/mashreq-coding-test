package com.mashreq.rooms.results;

import com.mashreq.common.models.BaseEntity;
import com.mashreq.rooms.Room;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record RoomResult(
    Room room
    // Todo add bookings and maintainence to room result
    //List<Booking> bookings,
    // Maintenence maintenence
) {
}