CREATE TABLE "bookings"
(
    id               uuid                     DEFAULT uuid_generate_v4(),
    user_id          uuid     NOT NULL,
    room_id          uuid     NOT NULL,
    number_of_people INTEGER  NOT NULL,
    start_time       DATETIME NOT NULL,
    end_time         DATETIME NOT NULL,
    status           ENUM('BOOKED', 'CANCELLED', 'COMPLETED') DEFAULT 'booked',
    created_on       TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    modified_on      TIMESTAMP WITH TIME ZONE DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (room_id) REFERENCES rooms (id)
);