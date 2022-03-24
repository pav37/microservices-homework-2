--liquibase formatted sql

--changeset pav:1
create table users (
   id bigserial primary key,
   username varchar(255) unique,
   user_group_id uuid,
   email varchar(50) not null
);

create table user_groups (
   id uuid not null primary key,
   name varchar not null
);

create table users_user_groups (
   user_id bigserial not null references users(id),
   user_group_id uuid not null  references user_groups(id)
);

create table device_groups (
     id uuid not null primary key,
     name varchar not null
);

create table user_groups_device_groups (
     user_group_id uuid not null references user_groups(id),
     device_group_id uuid not null references device_groups(id)
);

create table devices (
    id uuid not null primary key,
    name varchar not null,
    device_group_id uuid not null,
    is_test boolean not null default false,
    foreign key (device_group_id) references device_groups(id)
);

create table parameters (
     id uuid not null primary key,
     parameter_name varchar not null,
     value_min numeric (19,2),
     value_max numeric (19,2)
);

create table sensors (
     id uuid not null primary key,
     parameter_id uuid not null references parameters(id),
     device_id uuid not null references devices(id),
     value numeric (19,2)
);

create table transactions (
    id uuid not null primary key,
);

insert into user_groups (id, name) values ('077a91d6-9bf2-46a9-b33f-bb52acd7b53d', 'all');
insert into device_groups (id, name) values ('d5d82efd-930c-47a9-8af2-1247ad9298b1', 'Airport');
insert into device_groups (id, name) values ('c31186f0-be2e-4297-94f1-913f0f22ecbd', 'test');
insert into user_groups_device_groups VALUES ('077a91d6-9bf2-46a9-b33f-bb52acd7b53d', 'c31186f0-be2e-4297-94f1-913f0f22ecbd');

insert into parameters(id, parameter_name, value_min, value_max) VALUES ('a470d500-bdcd-4879-b1f2-17b8defdf9d6', 'temperature', 0.0, 100.0);
insert into parameters(id, parameter_name, value_min, value_max) VALUES ('879d529a-4bef-4ba0-a3be-b30c31505ed4', 'enabled', 0.0, 1.0);

insert into devices (id, name, device_group_id, is_test) values ('64ca86da-1482-4d7e-9f0c-9adc8ba1e4cc', 'test_device_1', 'c31186f0-be2e-4297-94f1-913f0f22ecbd', true);
insert into sensors (id, parameter_id, device_id) VALUES ('34d78b0d-0986-4904-af1d-7065fe0fbe96', 'a470d500-bdcd-4879-b1f2-17b8defdf9d6', '64ca86da-1482-4d7e-9f0c-9adc8ba1e4cc');
insert into sensors (id, parameter_id, device_id) VALUES ('71780a14-3bca-4b20-a154-326b4230c171', '879d529a-4bef-4ba0-a3be-b30c31505ed4', '64ca86da-1482-4d7e-9f0c-9adc8ba1e4cc');

insert into devices (id, name, device_group_id, is_test) values ('aabd7803-68e8-491d-8685-6f2815ad0f33', 'test_device_2', 'c31186f0-be2e-4297-94f1-913f0f22ecbd', true);
insert into sensors (id, parameter_id, device_id) VALUES ('3b3ab880-2d97-4ae8-a3d6-b1c7caef7089', 'a470d500-bdcd-4879-b1f2-17b8defdf9d6', 'aabd7803-68e8-491d-8685-6f2815ad0f33');
insert into sensors (id, parameter_id, device_id) VALUES ('95bd60fb-4c0e-4a42-8f1c-4f72a3d4c799', '879d529a-4bef-4ba0-a3be-b30c31505ed4', 'aabd7803-68e8-491d-8685-6f2815ad0f33');

--rollback
