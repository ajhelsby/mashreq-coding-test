CREATE TABLE "rooms"
(
    id           uuid                     DEFAULT uuid_generate_v4(),
    name         VARCHAR(100) NOT NULL,
    location     VARCHAR(255),
    capacity     INT          NOT NULL,
    created_on   TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    modified_on  TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    PRIMARY KEY (id)
);

INSERT INTO "rooms" (id, name, location, capacity, created_on, modified_on)
VALUES (uuid_generate_v4(), 'Amaze', '3 Person', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "rooms" (id, name, location, capacity, created_on, modified_on)
VALUES (uuid_generate_v4(), 'Beauty', '7 Person', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "rooms" (id, name, location, capacity, created_on, modified_on)
VALUES (uuid_generate_v4(), 'Inspire', '12 Person', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "rooms" (id, name, location, capacity, created_on, modified_on)
VALUES (uuid_generate_v4(), 'Strive', '20 Person', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
