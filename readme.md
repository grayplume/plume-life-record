
### sql文件

```
create table activities
(
    activity_id int auto_increment
        primary key,
    user_id     int                                 not null,
    name        varchar(100)                        not null,
    description text                                null,
    created_at  timestamp default CURRENT_TIMESTAMP null
);

create index user_id
    on activities (user_id);

create table statistics
(
    stat_id        int auto_increment
        primary key,
    user_id        int                                 not null,
    activity_id    int                                 not null,
    total_duration int                                 not null,
    created_at     timestamp default CURRENT_TIMESTAMP null,
    status         int       default 0                 not null comment '状态',
    updated_at     timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);

create index activity_id
    on statistics (activity_id);

create index user_id
    on statistics (user_id);

alter table statistics
    add constraint statistics_ibfk_2
        foreign key (activity_id) references activities (activity_id)
            on delete cascade;

create table time_records
(
    record_id   int auto_increment
        primary key,
    user_id     int                                 not null,
    activity_id int                                 not null,
    start_time  datetime                            not null,
    end_time    datetime                            null,
    duration    int                                 null,
    notes       text                                null,
    created_at  timestamp default CURRENT_TIMESTAMP null
);

create index activity_id
    on time_records (activity_id);

create index user_id
    on time_records (user_id);

alter table time_records
    add constraint time_records_ibfk_2
        foreign key (activity_id) references activities (activity_id)
            on delete cascade;

create table users
(
    user_id       int auto_increment
        primary key,
    username      varchar(50)                         not null,
    email         varchar(100)                        not null,
    password_hash varchar(255)                        not null,
    created_at    timestamp default CURRENT_TIMESTAMP null
);

alter table activities
    add constraint activities_ibfk_1
        foreign key (user_id) references users (user_id)
            on delete cascade;

alter table statistics
    add constraint statistics_ibfk_1
        foreign key (user_id) references users (user_id)
            on delete cascade;

alter table time_records
    add constraint time_records_ibfk_1
        foreign key (user_id) references users (user_id)
            on delete cascade;

alter table users
    add constraint email
        unique (email);

alter table users
    add constraint username
        unique (username);
```
 