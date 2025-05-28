
### sql文件

```sql
create table activities
(
    activity_id int auto_increment comment '活动ID'
        primary key,
    user_id     int                                 not null comment '用户ID',
    name        varchar(100)                        not null comment '活动名称',
    description text                                null comment '活动描述',
    created_at  timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    category_id int       default 1                 not null comment '活动分类D'
);

create index user_id
    on activities (user_id);

create table activity_categories
(
    category_id   int auto_increment comment '活动分类id'
        primary key,
    category_name varchar(32) not null comment '分类名称'
)
    comment '活动分类表';

alter table activities
    add constraint activities_activity_categories_category_id_fk
        foreign key (category_id) references activity_categories (category_id);

alter table activity_categories
    add constraint activity_categories_pk
        unique (category_name);

create table statistics
(
    stat_id        int auto_increment comment '统计ID'
        primary key,
    user_id        int                                 not null comment '用户ID',
    activity_id    int                                 not null comment '活动ID',
    total_duration int                                 not null comment '总时长',
    created_at     timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    status         int       default 0                 not null comment '状态  0未运行1运行中',
    updated_at     timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '创建时间'
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
    record_id   int auto_increment comment '时间记录ID'
        primary key,
    user_id     int                                 not null comment '用户ID',
    activity_id int                                 not null comment '活动ID',
    start_time  datetime                            not null comment '开始时间',
    end_time    datetime                            null comment '结束时间',
    duration    int                                 null comment '持续时间',
    notes       text                                null comment '备注',
    created_at  timestamp default CURRENT_TIMESTAMP null comment '创建时间'
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
    user_id       int auto_increment comment '用户ID'
        primary key,
    username      varchar(50)                         not null comment '用户名称',
    email         varchar(100)                        not null comment '邮箱',
    password_hash varchar(255)                        not null comment '加密密码',
    created_at    timestamp default CURRENT_TIMESTAMP null comment '创建时间'
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
 