create database if not exists life_sync;

use life_sync;

# create user information table
create table if not exists user
(
    id          bigint auto_increment comment '用户 id'
        primary key,
    username    varchar(256)                       not null comment 'username',
    password    varchar(256)                       not null comment 'password',
    email       varchar(256)                       null comment 'email',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'create time',
    update_time datetime default CURRENT_TIMESTAMP not null comment 'update time',
    is_deleted  tinyint  default 0                 not null comment 'logic delete 0 - not deleted'
)
    comment 'user';


