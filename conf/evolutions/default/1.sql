# User schema

# --- !Ups
create table `link` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `url` TEXT NOT NULL,
  `name` TEXT NOT NULL,
  `description` TEXT,
  `screenshot` BLOB
)

# --- !Downs
drop table `link`