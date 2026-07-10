package com.senlabank.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic bankTransfersTopic() {
        return TopicBuilder.name("bank-transfers")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2") // Подтверждение на 2 брокера из 3
                .config("retention.ms", "300000")   // Время жизни 5 минут (в миллисекундах)
                .build();
    }
}