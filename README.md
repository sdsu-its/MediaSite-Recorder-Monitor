# MediaSite-Recorder-Monitor
TODO: Documentation and GitBook for Setup
TODO: JDoc for various Classes and Methods as needed.

Mediasite Docs: [mediasite.sdsu.edu/mediasite/api/v1/$metadata](http://mediasite.sdsu.edu/mediasite/api/v1/$metadata)

## Building Docker Image
You will need to run a `./gradlew clean war` before you build the docker container to make sure that the necessary assets (like the `.war`) file exists when the container is built. 

## Vault Parameters
`www_url`
`db-password`
`db-url`
`db-user`

### DDL
```sql
CREATE TABLE users (
  PK         INT PRIMARY KEY AUTO_INCREMENT,
  email      TEXT,
  first_name TEXT,
  last_name  TEXT,
  password   TEXT,
  notify     TINYINT(1)
);
CREATE TABLE preferences (
  setting VARCHAR(32) PRIMARY KEY NOT NULL,
  value   TEXT
);
CREATE TABLE recorders (
  id                       VARCHAR(64) PRIMARY KEY NOT NULL,
  name                     TEXT,
  description              TEXT,
  serial_number            TEXT,
  version                  TEXT,
  last_version_update_date TEXT,
  physical_address         TEXT,
  image_version            TEXT,
  last_seen                TIMESTAMP  DEFAULT NOW(),
  status                   INT
);
```