-- USERS
DROP TABLE USERS;
CREATE TABLE USERS(
    ID number not null,
    NAME varchar2(65),
    constraint user_pk primary key (ID)
);

-- SEQUENCE USERS
DROP SEQUANCE USERS_SEQUENCE;
CREATE SEQUENCE USERS_SEQUENCE start with 1 INCREMENT by 1;