--liquibase formatted sql

--changeset pav:1
create table zone_types (
   id bigserial primary key,
   name varchar(255) not null,
   code varchar(10) not null unique
);

create table alarm_types (
    id bigserial not null primary key,
    name varchar not null,
    parameter_name varchar not null,
    min_value numeric(4, 1),
    max_value numeric(4, 1),
    zone_type_id bigserial not null references zone_types(id)
);

create table alarms (
     id uuid not null primary key,
     alarm_type_id bigserial not null references alarm_types(id),
     device_id uuid,
     parameter_name varchar,
     value numeric(4, 1),
     datetime date not null,
     status varchar not null
);

insert into zone_types (id, name, code) values (1, 'Красная зона', 'red');
insert into zone_types (id, name, code) values (2, 'Оранжевая зона', 'orange');

insert into alarm_types (id, name, parameter_name, min_value, max_value, zone_type_id)
    values (1, 'Превышение температуры', 'temperature', 40.0, 50.0, 2),
           (2, 'Критическое превышение температуры', 'temperature', 50.0, 100.0, 1);
--rollback
