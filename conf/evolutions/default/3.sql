# --- !Ups
DROP TABLE IF EXISTS link;
CREATE TABLE links (
  id          SERIAL NOT NULL PRIMARY KEY,
  user_id     SERIAL NOT NULL REFERENCES users ON DELETE CASCADE ON UPDATE RESTRICT,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT,
  screenshot  BYTEA
);
--
# --- !Downs
DROP TABLE IF EXISTS links;
CREATE TABLE link (
  id          SERIAL NOT NULL PRIMARY KEY,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT,
  screenshot  BYTEA
);