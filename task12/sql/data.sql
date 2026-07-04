-- Очистка таблиц перед наполнением
TRUNCATE TABLE orders, books RESTART IDENTITY CASCADE;

-- 1. Наполнение таблицы книг (books)
INSERT INTO books (title, status, request_count, has_request, price, publication_date, arrival_date, description)
VALUES 
    ('Чистый код', 'IN_STOCK', 0, false, 1500, '2008-08-01', '2023-10-15', 'Легендарная книга Роберта Мартина о написании читаемого кода.'),
    ('Изучаем Java', 'IN_STOCK', 0, false, 1200, '2005-02-09', '2023-11-20', 'Отличное руководство для новичков от Кэти Сьерра и Берта Бейтса.'),
    ('Паттерны проектирования', 'OUT_OF_STOCK', 2, true, 2000, '1994-10-21', null, 'Классическая книга от Банды Четырех (GoF).'),
    ('Эффективный Java', 'IN_STOCK', 0, false, 1800, '2017-12-27', '2023-05-10', 'Лучшие практики программирования на Java от Джошуа Блоха.');

-- 2. Наполнение таблицы заказов (orders)
INSERT INTO orders (book_id, status, customer_name, execution_date, price)
VALUES 
    (1, 'COMPLETED', 'Иван Иванов', '2023-11-01', 1500),
    (2, 'NEW', 'Петр Петров', null, 1200),
    (3, 'NEW', 'Алексей Смирнов', null, 2000),
    (1, 'NEW', 'Мария Сидорова', null, 1500),
    (4, 'CANCELED', 'Дмитрий Соколов', null, 1800);