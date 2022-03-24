--liquibase formatted sql

--changeset pav:1
create table users (
   id bigserial primary key,
   username varchar(255) unique,
   firstname varchar(255),
   lastname varchar(255),
   password varchar(100) not null,
   email varchar(50) not null,
   enabled boolean not null
);

create table authorities (
    username varchar(50) not null,
    authority varchar(68) not null,
    foreign key (username) references users(username)
);


--rollback drop table users;
