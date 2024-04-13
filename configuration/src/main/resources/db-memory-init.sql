CREATE SCHEMA IF NOT EXISTS ACC;

CREATE TABLE shedlock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
);

create table corporation
(
    id                         varchar(32)              not null,
    customer_id                varchar(32)              not null,
    full_name                  varchar(105),
    short_name                 varchar(17)
);

create table customer
(
    id                  varchar(32)              not null,
    flexcube_id         varchar(32),
    type_code           varchar(32)              not null
);

create table customer_person
(
    person_id        varchar(32)              not null,
    customer_id      varchar(32)              not null,
    person_type_code varchar(32)              not null
);

create table person
(
    id                  varchar(32)              not null,
    first_name          varchar(105)             not null,
    last_name           varchar(105)             not null
);