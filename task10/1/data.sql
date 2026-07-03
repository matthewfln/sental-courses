-- Очистка таблиц перед вставкой
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
('Apple', '6002', 'PC');

-- Наполнение таблицы pc (code, model, speed, ram, hd, cd, price)
INSERT INTO pc (code, model, speed, ram, hd, cd, price) VALUES
(1, '1001', 3000, 8, 500.0, '24x', 600.00),
(2, '1002', 3500, 16, 1000.0, '48x', 850.50),
(3, '2001', 2800, 8, 500.0, '52x', 550.00),
(4, '3001', 3200, 32, 2000.0, '24x', 1200.00),
(5, '6002', 3600, 64, 2000.0, 'None', NULL);

-- Наполнение таблицы laptop (code, model, speed, ram, hd, price, screen)
INSERT INTO laptop (code, model, speed, ram, hd, price, screen) VALUES
(1, '1003', 2400, 8, 256.0, 700.00, 15),
(2, '2002', 2800, 16, 512.0, 950.00, 14),
(3, '2003', 3200, 32, 1000.0, 1500.00, 17),
(4, '3002', 2000, 8, 256.0, 650.00, 13),
(5, '6001', 3200, 16, 512.0, 1800.00, 14);

-- Наполнение таблицы printer (code, model, color, type, price)
INSERT INTO printer (code, model, color, type, price) VALUES
(1, '1004', 'n', 'Laser', 200.00),
(2, '3003', 'y', 'Jet', 150.00),
(3, '4001', 'y', 'Laser', 450.00),
(4, '4002', 'n', 'Matrix', 120.00),
(5, '5001', 'y', 'Jet', NULL);