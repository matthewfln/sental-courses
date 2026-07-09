-- Удаление таблиц (сначала orders, т.к она зависит от books)
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS books;

-- 1. Создание таблицы книг
CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    request_count INTEGER DEFAULT 0,
    has_request BOOLEAN DEFAULT FALSE,
    price INTEGER NOT NULL,
    publication_date DATE,
    arrival_date DATE,
    description TEXT
);

-- 2. Создание таблицы заказов
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    execution_date DATE,
    price INTEGER NOT NULL,
    
    -- Внешний ключ: связываем заказ с книгой
    CONSTRAINT fk_book
        FOREIGN KEY(book_id) 
        REFERENCES books(id)
        ON DELETE CASCADE
);