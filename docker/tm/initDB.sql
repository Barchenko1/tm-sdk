ALTER SYSTEM SET max_connections = 200;

CREATE TABLE TestEntity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE TestEmployee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE TestDependent (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    status VARCHAR(255),
    testEmployee_id BIGINT
);

ALTER TABLE TestDependent
ADD CONSTRAINT fk_testEmployee_id
FOREIGN KEY (testEmployee_id) REFERENCES TestEmployee(id);
