# --- !Ups
CREATE TABLE users (
  id       SERIAL NOT NULL PRIMARY KEY,
  username TEXT   NOT NULL
);
CREATE TABLE passwords (
  user_id       SERIAL NOT NULL PRIMARY KEY REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  password TEXT   NOT NULL
);
--
# --- !Downs
DROP TABLE IF EXISTS passwords; --
DROP TABLE IF EXISTS users; --