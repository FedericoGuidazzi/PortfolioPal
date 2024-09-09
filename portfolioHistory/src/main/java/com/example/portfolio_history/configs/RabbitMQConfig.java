package com.example.portfolio_history.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue intradayMovementsQueue() {
        return new Queue("intradayMovements", true);
    }

    @Bean
    public Queue privacyUpdateQueue() {
        return new Queue("privacyUpdates", true);
    }

    @Bean
    public Queue updateOldMovementsQueue() {
        return new Queue("transactionUpdates", true);
    }
}
