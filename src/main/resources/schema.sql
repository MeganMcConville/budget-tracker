CREATE TABLE users(
username VARCHAR(200) UNIQUE,
password VARCHAR(200),
id BIGSERIAL PRIMARY KEY
);