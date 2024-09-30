package com.example.portfolio_history.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final String UPDATE_EXCHANGE = "privacyUpdateExchange";
    private static final String UPDATE_QUEUE = "updateQueuePortfolioService";
    private static final String TRANSACTION_QUEUE = "transactionUpdates";

    @Bean
    public FanoutExchange updateExchange() {
        return new FanoutExchange(UPDATE_EXCHANGE);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue(UPDATE_QUEUE, true);
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, FanoutExchange updateExchange) {
        return BindingBuilder.bind(updateQueue).to(updateExchange);
    }

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE, true);
    }
}
