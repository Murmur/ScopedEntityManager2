-- Test database for JPA webapp

CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_swedish_ci;
USE test;

GRANT USAGE ON *.* TO test@localhost IDENTIFIED BY "test";
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON * TO test@localhost;

CREATE TABLE IF NOT EXISTS orderheader (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  custid bigint(20) NOT NULL,
  comment text,
  updated_utc datetime NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;
-- ALTER TABLE orderheader AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS orderheader2 (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  custid bigint(20) NOT NULL,
  comment text,
  updated_utc datetime NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;
-- ALTER TABLE orderheader2 AUTO_INCREMENT = 1;


CREATE TABLE IF NOT EXISTS orderrow (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  headerid bigint(20) NOT NULL,
  comment text,
  qty int(11) NOT NULL,
  updated_utc datetime NOT NULL,
  PRIMARY KEY (id),
  KEY orderrow_fk (headerid),
  CONSTRAINT orderrow_fk FOREIGN KEY (headerid) REFERENCES orderheader(id) ON DELETE CASCADE
) ENGINE=InnoDB;
