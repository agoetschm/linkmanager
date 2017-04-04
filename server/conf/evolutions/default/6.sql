# --- !Ups
CREATE TABLE folders (
  id      SERIAL NOT NULL PRIMARY KEY,
  user_id INT    NOT NULL REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  name    TEXT   NOT NULL,
  parent  SERIAL REFERENCES folders ON DELETE SET NULL ON UPDATE RESTRICT
);

DROP TABLE IF EXISTS links CASCADE;
CREATE TABLE links (
  id          SERIAL NOT NULL PRIMARY KEY,
  user_id     INT    NOT NULL REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT,
  parent      INT REFERENCES folders ON DELETE SET NULL ON UPDATE RESTRICT
);

TRUNCATE passwords; --- first clear passwords because of foreign key constraint
--- forgot to make email and activated NOT NULL
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  id        SERIAL  NOT NULL PRIMARY KEY,
  username  TEXT    NOT NULL,
  email     TEXT    NOT NULL,
  activated BOOLEAN NOT NULL
);

# --- !Downs
--- never used anyway
DROP TABLE IF EXISTS folders CASCADE;
