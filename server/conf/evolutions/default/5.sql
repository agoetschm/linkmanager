# --- !Ups
ALTER TABLE users ADD email TEXT;
ALTER TABLE users ADD activated BOOLEAN;


# --- !Downs

ALTER TABLE users DROP COLUMN email ;
ALTER TABLE users DROP COLUMN activated ;