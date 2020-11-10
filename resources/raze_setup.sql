#---------
#CREATE DATABASE & TABLES
#---------

CREATE DATABASE raze_database;
USE raze_database;

# This guild_bansguild_bansguild_banssetting is required so you can delete things without doing a WHERE on a unique key
SET SQL_SAFE_UPDATES = 0;





# role section
CREATE TABLE role_sections
(
role_section char(18) primary key,
guild_id char(18)
);

CREATE TABLE role_section_sub_roles
(
role_id char(18) primary key,
role_section char(18)
);


# core
CREATE TABLE guild_bans
(
	member_id char(18),
	guild_id char(18)
);

CREATE TABLE guild_logs
(
	guild_id char(18) primary key,
    log_channel_id char(18)
);

CREATE TABLE guild_permissions
(
	guild_id char(18),
    member_id char(18),
    permission varchar(1000)
);