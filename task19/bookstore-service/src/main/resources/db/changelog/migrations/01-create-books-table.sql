--liquibase formatted sql

CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    request_count INT DEFAULT 0,
    has_request BOOLEAN DEFAULT FALSE,
    price INT,
    publication_date DATE,
    arrival_date DATE,
    description VARCHAR(1000)
);