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