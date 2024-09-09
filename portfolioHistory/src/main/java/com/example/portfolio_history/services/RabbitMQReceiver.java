package com.example.portfolio_history.services;

import com.example.portfolio_history.models.MovementBin;
import com.example.portfolio_history.models.bin.PutUserPrivacyBin;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class RabbitMQReceiver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

    @RabbitListener(queues = "privacyUpdates")
    public void updatePrivacy(String msg) {
        System.out.println("\n\n[R] Received privacyUpdates < " + msg + " >");

        try {
            PutUserPrivacyBin privacyBin = objectMapper.readValue(msg, PutUserPrivacyBin.class);
            this.portfolioService.updatePortfolioPrivacy(privacyBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "intradayMovements")
    public void addIntradayMovement(String msg) {
        System.out.println("\n\n[R] Received intradayMovements < " + msg + " >");

        try {
            MovementBin movementBin = objectMapper.readValue(msg, MovementBin.class);
            this.portfolioHistoryService.insertIntradayMovement(movementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "transactionUpdates")
    public void updateOldMovements(String msg) {
        System.out.println("\n\n[R] Received transactionUpdates < " + msg + " >");

        try {
            MovementBin movementBin = objectMapper.readValue(msg, MovementBin.class);
            this.portfolioHistoryService.updateOldMovements(movementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}