-- ЗАДАНИЕ 1: Создание структуры БД (DDL)

-- Удаление таблиц (в обратном порядке из-за внешних ключей)
DROP TABLE IF EXISTS pc;
DROP TABLE IF EXISTS laptop;
DROP TABLE IF EXISTS printer;
DROP TABLE IF EXISTS product;

-- 1. Таблица product (Общий список моделей)
CREATE TABLE product (
    maker VARCHAR(10) NOT NULL,
    model VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (model)
);

-- 2. Таблица pc (Характеристики настольных компьютеров)
CREATE TABLE pc (
    code INT NOT NULL,
    model VARCHAR(50) NOT NULL,
    speed SMALLINT NOT NULL,
    ram SMALLINT NOT NULL,
    hd REAL NOT NULL,
    cd VARCHAR(10) NOT NULL,
    price DECIMAL(10, 2) NULL,
    CONSTRAINT pk_pc PRIMARY KEY (code),
    CONSTRAINT fk_pc_product FOREIGN KEY (model) REFERENCES product(model)
);

-- 3. Таблица laptop (Характеристики ноутбуков)
CREATE TABLE laptop (
    code INT NOT NULL,
    model VARCHAR(50) NOT NULL,
    speed SMALLINT NOT NULL,
    ram SMALLINT NOT NULL,
    hd REAL NOT NULL,
    price DECIMAL(10, 2) NULL,
    screen SMALLINT NOT NULL,
    CONSTRAINT pk_laptop PRIMARY KEY (code),
    CONSTRAINT fk_laptop_product FOREIGN KEY (model) REFERENCES product(model)
);

-- 4. Таблица printer (Характеристики принтеров)
CREATE TABLE printer (
    code INT NOT NULL,
    model VARCHAR(50) NOT NULL,
    color CHAR(1) NOT NULL,
    type VARCHAR(10) NOT NULL,
    price DECIMAL(10, 2) NULL,
    CONSTRAINT pk_printer PRIMARY KEY (code),
    CONSTRAINT fk_printer_product FOREIGN KEY (model) REFERENCES product(model)
);


-- ЗАДАНИЕ 1: Заполнение базы данных тестовыми данными (DML)

-- Очистка таблиц перед вставкой (на всякий случай)
DELETE FROM pc;
DELETE FROM laptop;
DELETE FROM printer;
DELETE FROM product;

-- Наполнение таблицы product
INSERT INTO product (maker, model, type) VALUES
('HP', '1001', 'PC'),
('HP', '1002', 'PC'),
('HP', '1003', 'Laptop'),
('HP', '1004', 'Printer'),
('Asus', '2001', 'PC'),
('Asus', '2002', 'Laptop'),
('Asus', '2003', 'Laptop'),
('Dell', '3001', 'PC'),
('Dell', '3002', 'Laptop'),
('Dell', '3003', 'Printer'),
('Canon', '4001', 'Printer'),
('Canon', '4002', 'Printer'),
('Epson', '5001', 'Printer'),
('Apple', '6001', 'Laptop'),
('Apple', '6002', 'PC'),
('A', '7001', 'PC'),
('B', '8001', 'PC'),
('B', '8002', 'Laptop');

-- Наполнение таблицы pc (code, model, speed, ram, hd, cd, price)
INSERT INTO pc (code, model, speed, ram, hd, cd, price) VALUES
(1, '1001', 3000, 8, 500.0, '24x', 600.00),
(2, '1002', 3500, 16, 1000.0, '48x', 450.50),
(3, '2001', 2800, 8, 500.0, '12x', 550.00),
(4, '3001', 3200, 32, 2000.0, '24x', 1200.00),
(5, '6002', 3600, 64, 2000.0, 'None', NULL),
(6, '7001', 800, 4, 250.0, '24x', 350.00),
(7, '8001', 900, 4, 250.0, '24x', 400.00);

-- Наполнение таблицы laptop (code, model, speed, ram, hd, price, screen)
INSERT INTO laptop (code, model, speed, ram, hd, price, screen) VALUES
(1, '1003', 2400, 8, 256.0, 700.00, 15),
(2, '2002', 2800, 16, 512.0, 950.00, 14),
(3, '2003', 3200, 32, 1000.0, 1500.00, 17),
(4, '3002', 2000, 8, 256.0, 650.00, 13),
(5, '6001', 3200, 16, 512.0, 1800.00, 14),
(6, '8002', 800, 8, 120.0, 500.00, 15);

-- Наполнение таблицы printer (code, model, color, type, price)
INSERT INTO printer (code, model, color, type, price) VALUES
(1, '1004', 'n', 'Laser', 200.00),
(2, '3003', 'y', 'Jet', 150.00),
(3, '4001', 'y', 'Laser', 450.00),
(4, '4002', 'n', 'Matrix', 120.00),
(5, '5001', 'y', 'Jet', NULL);





-- ЗАДАНИЕ 2: SELECT-запросы

-- 1. Найти номер модели, скорость и размер жесткого диска для всех ПК стоимостью менее 500 долларов.
SELECT model, speed, hd 
FROM pc 
WHERE price < 500;

-- 2. Найти производителей принтеров. Вывести поля: maker.
SELECT DISTINCT maker 
FROM product 
WHERE type = 'Printer';

-- 3. Найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
SELECT model, ram, screen 
FROM laptop 
WHERE price > 1000;

-- 4. Найти все записи таблицы Printer для цветных принтеров.
SELECT * FROM printer 
WHERE color = 'y';

-- 5. Найти номер модели, скорость и размер жесткого диска для ПК, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
SELECT model, speed, hd 
FROM pc 
WHERE cd IN ('12x', '24x') AND price < 600;

-- 6. Указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 Гбайт.
SELECT DISTINCT p.maker, l.speed 
FROM product p 
JOIN laptop l ON p.model = l.model 
WHERE l.hd >= 100;

-- 7. Найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем B (латинская буква).
SELECT p.model, pc.price FROM product p JOIN pc ON p.model = pc.model WHERE p.maker = 'B'
UNION
SELECT p.model, l.price FROM product p JOIN laptop l ON p.model = l.model WHERE p.maker = 'B'
UNION
SELECT p.model, pr.price FROM product p JOIN printer pr ON p.model = pr.model WHERE p.maker = 'B';

-- 8. Найти производителя, выпускающего ПК, но не ноутбуки.
SELECT DISTINCT maker 
FROM product 
WHERE type = 'PC' AND maker NOT IN (
    SELECT maker 
    FROM product 
    WHERE type = 'Laptop'
);

-- 9. Найти производителей ПК с процессором не менее 450 Мгц. Вывести поля: maker.
SELECT DISTINCT p.maker 
FROM product p 
JOIN pc ON p.model = pc.model 
WHERE pc.speed >= 450;

-- 10. Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.
SELECT model, price 
FROM printer 
WHERE price = (SELECT MAX(price) FROM printer);

-- 11. Найти среднюю скорость ПК.
SELECT AVG(speed) AS avg_speed 
FROM pc;

-- 12. Найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
SELECT AVG(speed) AS avg_speed 
FROM laptop 
WHERE price > 1000;

-- 13. Найти среднюю скорость ПК, выпущенных производителем A.
SELECT AVG(pc.speed) AS avg_speed 
FROM pc 
JOIN product p ON pc.model = p.model 
WHERE p.maker = 'A';

-- 14. Для каждого значения скорости процессора найти среднюю стоимость ПК с такой же скоростью. Вывести поля: скорость, средняя цена.
SELECT speed, AVG(price) AS avg_price 
FROM pc 
GROUP BY speed;

-- 15. Найти размеры жестких дисков, совпадающих у двух и более PC. Вывести поля: hd.
SELECT hd 
FROM pc 
GROUP BY hd 
HAVING COUNT(*) >= 2;

-- 16. Найти пары моделей PC, имеющих одинаковые скорость процессора и RAM.
SELECT pc1.model AS model_1, pc2.model AS model_2, pc1.speed, pc1.ram 
FROM pc pc1 
JOIN pc pc2 ON pc1.speed = pc2.speed AND pc1.ram = pc2.ram 
WHERE pc1.model > pc2.model;

-- 17. Найти модели ноутбуков, скорость которых меньше скорости любого из ПК. Вывести поля: type, model, speed.
SELECT DISTINCT p.type, l.model, l.speed 
FROM laptop l 
JOIN product p ON l.model = p.model 
WHERE l.speed < ALL (SELECT speed FROM pc);

-- 18. Найти производителей самых дешевых цветных принтеров. Вывести поля: maker, price.
SELECT DISTINCT p.maker, pr.price 
FROM printer pr 
JOIN product p ON pr.model = p.model 
WHERE pr.color = 'y' AND pr.price = (
    SELECT MIN(price) 
    FROM printer 
    WHERE color = 'y'
);

-- 19. Для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. Вывести поля: maker, средний размер экрана.
SELECT p.maker, AVG(l.screen) AS avg_screen 
FROM product p 
JOIN laptop l ON p.model = l.model 
GROUP BY p.maker;

-- 20. Найти производителей, выпускающих по меньшей мере три различных модели ПК. Вывести поля: maker, число моделей.
SELECT maker, COUNT(model) AS model_count 
FROM product 
WHERE type = 'PC' 
GROUP BY maker 
HAVING COUNT(model) >= 3;

-- 21. Найти максимальную цену ПК, выпускаемых каждым производителем. Вывести поля: maker, максимальная цена.
SELECT p.maker, MAX(pc.price) AS max_price 
FROM product p 
JOIN pc ON p.model = pc.model 
GROUP BY p.maker;

-- 22. Для каждого значения скорости процессора ПК, превышающего 600 МГц, найти среднюю цену ПК с такой же скоростью. Вывести поля: speed, средняя цена.
SELECT speed, AVG(price) AS avg_price 
FROM pc 
WHERE speed > 600 
GROUP BY speed;

-- 23. Найти производителей, которые производили бы как ПК, так и ноутбуки со скоростью не менее 750 МГц. Вывести поля: maker.
SELECT DISTINCT p1.maker 
FROM product p1 
JOIN pc ON p1.model = pc.model 
WHERE pc.speed >= 750 AND p1.maker IN (
    SELECT p2.maker 
    FROM product p2 
    JOIN laptop l ON p2.model = l.model 
    WHERE l.speed >= 750
);

-- 24. Перечислить номера моделей любых типов, имеющих самую высокую цену по всей имеющейся в базе данных продукции.
WITH all_products AS (
    SELECT model, price FROM pc WHERE price IS NOT NULL
    UNION ALL
    SELECT model, price FROM laptop WHERE price IS NOT NULL
    UNION ALL
    SELECT model, price FROM printer WHERE price IS NOT NULL
)
SELECT DISTINCT model 
FROM all_products 
WHERE price = (SELECT MAX(price) FROM all_products);

-- 25. Найти производителей принтеров, которые производят ПК с наименьшим объемом RAM и с самым быстрым процессором среди всех ПК, имеющих наименьший объем RAM. Вывести поля: maker.
SELECT DISTINCT p.maker 
FROM product p 
JOIN pc ON p.model = pc.model 
WHERE p.maker IN (SELECT maker FROM product WHERE type = 'Printer')
  AND pc.ram = (SELECT MIN(ram) FROM pc)
  AND pc.speed = (
      SELECT MAX(speed) 
      FROM pc 
      WHERE ram = (SELECT MIN(ram) FROM pc)
  );
