package com.example.portfolio.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue privacyUpdateQueue() {
        return new Queue("privacyUpdates", true);
    }
}
