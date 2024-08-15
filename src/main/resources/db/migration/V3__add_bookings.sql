CREATE TABLE bookings
(
    id               uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id          uuid                     NOT NULL,
    room_id          uuid                     NOT NULL,
    number_of_people INTEGER                  NOT NULL,
    start_time       TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time         TIMESTAMP WITH TIME ZONE NOT NULL,
    status           TEXT                     DEFAULT 'BOOKED',
    created_on       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_on      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (room_id) REFERENCES rooms (id)
);
