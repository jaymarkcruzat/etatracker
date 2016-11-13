DROP DATABASE APP_DB;
CREATE DATABASE APP_DB;
USE APP_DB;
CREATE TABLE USER (
        ID MEDIUMINT NOT NULL AUTO_INCREMENT,
        USR_NM CHAR(30),
        USR_PSSWRD CHAR(30),
        USR_EML CHAR(30),
        USR_STATUS TINYINT DEFAULT 0,
        USR_FIREBASEID CHAR(100),
        USR_DEVICEID CHAR(100),
        PRIMARY KEY (ID));

CREATE TABLE MARKER (
        ID MEDIUMINT NOT NULL AUTO_INCREMENT,
        MKR_TITLE CHAR(50),
        MKR_DESC CHAR(50),
        MKR_LATLNG POINT NOT NULL,
        MKR_TARGET TINYINT(1) DEFAULT 0,
        USER_ID MEDIUMINT NOT NULL,
        PRIMARY KEY (ID),
        CONSTRAINT FK_MARKER_USER FOREIGN KEY (USER_ID) REFERENCES USER(ID)
        );

CREATE TABLE MEETING (
        ID MEDIUMINT NOT NULL AUTO_INCREMENT,
        MARKER_ID MEDIUMINT NOT NULL,
        MTG_PARTICIPANT MEDIUMINT NOT NULL,
        MTG_STATUS TINYINT(1) DEFAULT 0,
        PRIMARY KEY (ID),
        CONSTRAINT FK_MEETING_MARKER FOREIGN KEY (MARKER_ID) REFERENCES MARKER(ID),
        CONSTRAINT FK_MEETING_USER FOREIGN KEY (MTG_PARTICIPANT) REFERENCES USER(ID)
        );

CREATE TABLE REQUEST (
        ID MEDIUMINT NOT NULL AUTO_INCREMENT,
        REQ_SENDER MEDIUMINT NOT NULL,
        REQ_RECIPIENT MEDIUMINT NOT NULL,
        REQ_STATUS TINYINT(1) DEFAULT 0,
        PRIMARY KEY (ID),
        CONSTRAINT FK_REQUEST_USER_SENDER FOREIGN KEY (REQ_SENDER) REFERENCES USER(ID),
        CONSTRAINT FK_REQUEST_USER_RECIPIENT FOREIGN KEY (REQ_RECIPIENT) REFERENCES USER(ID)
        );