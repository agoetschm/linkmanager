# --- !Ups
DROP TABLE IF EXISTS links;
CREATE TABLE links (
  id          SERIAL NOT NULL PRIMARY KEY,
  user_id     SERIAL NOT NULL REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT
);

-- CREATE TABLE users (
--   id       SERIAL NOT NULL PRIMARY KEY,
--   username TEXT   NOT NULL
-- );
-- CREATE TABLE passwords (
--   user_id       SERIAL NOT NULL PRIMARY KEY REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
--   password TEXT   NOT NULL
-- );


# --- !Downs
DROP TABLE IF EXISTS links;
CREATE TABLE links (
  id          SERIAL NOT NULL PRIMARY KEY,
  user_id     SERIAL NOT NULL REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT,
  screenshot  BYTEA
);