@echo off
chcp 65001 > nul
echo === Запуск инициализации базы данных BookStore ===

SET DB_NAME=bookstore
SET DB_USER=postgres

SET PSQL_PATH="D:\PostgreSQL\17\bin\psql.exe"

echo [1/2] Создание схемы таблиц...
%PSQL_PATH% -U %DB_USER% -d %DB_NAME% -f "C:\Users\matth\Desktop\Практика\task11\sql\schema.sql"
if %ERRORLEVEL% NEQ 0 goto error

echo [2/2] Заполнение тестовыми данными...
%PSQL_PATH% -U %DB_USER% -d %DB_NAME% -f "C:\Users\matth\Desktop\Практика\task11\sql\data.sql"
if %ERRORLEVEL% NEQ 0 goto error

echo === База данных успешно инициализирована! ===
pause
exit /b 0

:error
echo [ОШИБКА] Произошла ошибка при выполнении скриптов! Проверь параметры подключения или путь к psql.
pause
exit /b 1