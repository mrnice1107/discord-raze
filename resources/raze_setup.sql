#---------
#CREATE DATABASE & TABLES
#---------

CREATE DATABASE raze_database;
USE raze_database;

# This guild_bansguild_bansguild_banssetting is required so you can delete things without doing a WHERE on a unique key
SET SQL_SAFE_UPDATES = 0;

# application
CREATE TABLE applications
(
	application_id int NOT NULL AUTO_INCREMENT primary key,
	application_name varchar(1000),
    message_id char(18),
    emojis varchar(100),
	guild_id char(18)
);

# formula
CREATE TABLE formulas
(
	formula_id int NOT NULL AUTO_INCREMENT primary key,
	formula_name varchar(1000),
	guild_id char(18)
);

CREATE TABLE formula_questions
(
	formula_id varchar(1000),
	question_id int,
	question varchar(1000)
);

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