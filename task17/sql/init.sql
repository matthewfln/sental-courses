-- Скрипт создания таблиц
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

CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY,
    book_id INT,
    status VARCHAR(50),
    customer_name VARCHAR(255),
    execution_date DATE,
    price INT,
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id)
);