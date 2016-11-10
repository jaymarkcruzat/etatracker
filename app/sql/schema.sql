drop database appdb;
create database appdb;
use appdb;
create table user (
        id mediumint not null auto_increment,
        usr_nm char(30),
        usr_psswrd char(30),
        usr_eml char(30),
        usr_status tinyint default 0,
        usr_firebaseid char(100),
        usr_deviceid char(100),
        primary key (id));

create table marker (
        id mediumint not null auto_increment,
        mkr_title char(50),
        mkr_desc char(50),
        mkr_latlng point not null,
        mkr_target tinyint(1) default 0,
        user_id mediumint not null,
        primary key (id),
        constraint fk_marker_user foreign key (user_id) references user(id)
        );

create table meeting (
        id mediumint not null auto_increment,
        marker_id mediumint not null,
        mtg_participant mediumint not null,
        mtg_status tinyint(1) default 0,
        primary key (id),
        constraint fk_meeting_marker foreign key (marker_id) references marker(id),
        constraint fk_meeting_user foreign key (mtg_participant) references user(id)
        );

create table request (
        id mediumint not null auto_increment,
        req_sender mediumint not null,
        req_recipient mediumint not null,
        req_status tinyint(1) default 0,
        primary key (id),
        constraint fk_request_user_sender foreign key (req_sender) references user(id),
        constraint fk_request_user_recipient foreign key (req_recipient) references user(id)
        );