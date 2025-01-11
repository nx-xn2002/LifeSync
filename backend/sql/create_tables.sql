create database if not exists life_sync;

use life_sync;

# create user information table
create table if not exists user
(
    id          bigint auto_increment comment 'user id'
        primary key,
    username    varchar(256)                       not null comment 'username',
    password    varchar(256)                       not null comment 'password',
    email       varchar(256)                       null comment 'email',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'create time',
    update_time datetime default CURRENT_TIMESTAMP not null comment 'update time',
    is_deleted  tinyint  default 0                 not null comment 'logic delete 0 - not deleted'
)
    comment 'user';

# create basic_health information table
create table if not exists basic_health
(
    health_id   bigint auto_increment comment 'basic health id'
        primary key,
    user_id     bigint                             not null comment 'user id',
    height      int                                null comment 'height(cm)',
    weight      decimal(5, 2)                      null comment 'weight(m)',
    age         int                                null comment 'age',
    gender      int                                null comment 'gender 0 - female, 1 - male, 2 - secret',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'create time',
    update_time datetime default CURRENT_TIMESTAMP not null comment 'update time',
    is_deleted  tinyint  default 0                 not null comment 'logic delete 0 - not deleted',
    foreign key (user_id) references user (id)
)
    comment 'basic_health';

# create detection_record information table
create table detection_record
(
    record_id    bigint auto_increment comment 'record id'
        primary key,
    user_id      bigint                             not null comment 'user id',
    systolic_bp  smallint unsigned                  null comment 'systolic blood pressure value(mmHg)',
    diastolic_bp smallint unsigned                  null comment 'diastolic blood pressure value(mmHg)',
    heart_rate   smallint unsigned                  null comment 'heart rate(BPM)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'create time',
    update_time  datetime default CURRENT_TIMESTAMP not null comment 'update time',
    is_deleted   tinyint  default 0                 not null comment 'logic delete 0 - not deleted',
    foreign key (user_id) references user (id)
)
    comment 'detection record';






