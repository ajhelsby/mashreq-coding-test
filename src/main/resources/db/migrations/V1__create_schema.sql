CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "users"
(
    id                     uuid                     DEFAULT uuid_generate_v4(),
    email                  VARCHAR(155) NOT NULL UNIQUE,
    password               VARCHAR(255)             DEFAULT NULL,
    first_name             VARCHAR(45)  NOT NULL,
    last_name              VARCHAR(45)  NOT NULL,
    external_id            VARCHAR(150)             DEFAULT NULL UNIQUE,
    last_logged_in_on      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    credentials_updated_on TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_on             TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    modified_on            TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    PRIMARY KEY (id)
);
