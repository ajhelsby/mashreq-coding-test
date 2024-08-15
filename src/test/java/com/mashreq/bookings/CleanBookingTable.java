package com.mashreq.bookings;

import com.mashreq.CleanBookingTableExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(CleanBookingTableExtension.class)
public @interface CleanBookingTable {
  String tableName() default "bookings";
}