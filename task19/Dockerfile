# Этап 1: Сборка приложения с помощью Maven
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Копируем родительский pom.xml и исходники всех модулей
COPY pom.xml .
COPY bookstore-model/ ./bookstore-model/
COPY bookstore-common/ ./bookstore-common/
COPY bookstore-service/ ./bookstore-service/
COPY bookstore-webapp/ ./bookstore-webapp/

# Собираем проект и пропускаем тесты для ускорения
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

# Этап 2: Запуск собранного приложения в Tomcat
FROM tomcat:9.0-jre17-temurin-jammy
WORKDIR /usr/local/tomcat

# Удаляем стандартные приложения Tomcat, чтобы освободить корневой контекст
RUN rm -rf webapps/*

# Копируем собранный war-пакет в папку webapps под именем ROOT.war,
# чтобы приложение было доступно по адресу http://localhost:8080/ без лишних префиксов
COPY --from=builder /app/bookstore-webapp/target/bookstore-webapp.war ./webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]