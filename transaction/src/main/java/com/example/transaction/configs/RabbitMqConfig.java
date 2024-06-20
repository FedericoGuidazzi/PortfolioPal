package com.example.transaction.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue privacyUpdateQueue() {
        return new Queue("transactionUpdates", true);
    }
}