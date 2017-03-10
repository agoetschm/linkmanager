# --- !Ups
CREATE TABLE link (
  id          SERIAL NOT NULL PRIMARY KEY,
  url         TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT,
  screenshot  BYTEA
); --
--
# --- !Downs
DROP TABLE IF EXISTS link; --