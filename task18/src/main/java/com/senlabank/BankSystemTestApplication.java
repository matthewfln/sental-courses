package com.senlabank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.senlabank")
@EnableJpaRepositories(basePackages = "com.senlabank.repository")
@EntityScan(basePackages = "com.senlabank.model")
public class BankSystemTestApplication {

    private static final Logger LOGGER = LogManager.getLogger(BankSystemTestApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Запуск тестового окружения банковской системы...");
        SpringApplication.run(BankSystemTestApplication.class, args);
        LOGGER.info("Система работает. Продюсер генерирует транзакции, 3 реплики консюмера обрабатывают их в фоне.");
    }
}