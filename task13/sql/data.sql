-- Скрипт заполнения тестовыми данными
INSERT INTO books (id, title, status, request_count, has_request, price, publication_date, arrival_date, description)
VALUES (1, 'Война и мир', 'IN_STOCK', 0, false, 1500, '1869-01-01', '2023-01-01', 'Классика литературы');

INSERT INTO books (id, title, status, request_count, has_request, price, publication_date, arrival_date, description)
VALUES (2, 'Преступление и наказание', 'OUT_OF_STOCK', 5, true, 1200, '1866-01-01', null, 'Психологический роман');

INSERT INTO orders (id, book_id, status, customer_name, execution_date, price)
VALUES (101, 1, 'COMPLETED', 'Петр Петров', '2023-10-01', 1500);