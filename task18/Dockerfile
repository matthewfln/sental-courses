# Используем легковесный образ с Java 11
FROM eclipse-temurin:11-jre-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный jar-файл из папки target
COPY target/task18-1.0-SNAPSHOT.jar app.jar

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]