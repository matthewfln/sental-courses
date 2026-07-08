--liquibase formatted sql

CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY,
    book_id INT,
    status VARCHAR(50),
    customer_name VARCHAR(255),
    execution_date DATE,
    price INT,
    CONSTRAINT fk_orders_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
);