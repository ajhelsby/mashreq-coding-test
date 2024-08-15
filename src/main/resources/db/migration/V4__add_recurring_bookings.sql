CREATE TABLE recurring_bookings
(
    id           uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id      uuid,
    room_id      uuid NOT NULL,
    name         VARCHAR(100),
    description  VARCHAR(255),
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    booking_type VARCHAR(100)             DEFAULT 'MAINTENANCE',
    status       TEXT                     DEFAULT 'BOOKED',
    created_on   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_on  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (room_id) REFERENCES rooms (id)
);

-- Insert maintenance windows for rooms Amaze, Beauty, Inspire, and Strive
INSERT INTO recurring_bookings (user_id, room_id, name, description, start_time, end_time, booking_type)
SELECT NULL, -- Assuming user_id is not required for maintenance entries
       rooms.id,
       'Maintenance Window',
       'Scheduled maintenance for room',
       times.start_time,
       times.end_time,
       'MAINTENANCE'
FROM rooms
         CROSS JOIN (SELECT TIME '09:00' AS start_time,
                            TIME '09:15' AS end_time
                     UNION ALL
                     SELECT TIME '13:00' AS start_time,
                            TIME '13:15' AS end_time
                     UNION ALL
                     SELECT TIME '17:00' AS start_time,
                            TIME '17:15' AS end_time) AS times
WHERE rooms.name IN ('Amaze', 'Beauty', 'Inspire', 'Strive');
