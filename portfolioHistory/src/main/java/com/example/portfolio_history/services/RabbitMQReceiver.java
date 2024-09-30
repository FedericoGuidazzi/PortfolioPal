package com.example.portfolio_history.services;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.portfolio_history.models.bin.PutUserPrivacyBin;
import com.example.portfolio_history.models.bin.RabbitTransactionBin;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableRabbit
public class RabbitMQReceiver {
    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

    @RabbitListener(queues = "#{updateQueue.name}")
    public void updatePrivacy(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        System.out.println("\n\n[R] Received privacyUpdates < " + msg + " >");

        try {
            PutUserPrivacyBin privacyBin = objectMapper.readValue(msg, PutUserPrivacyBin.class);
            this.portfolioService.updatePortfolioPrivacy(privacyBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "#{transactionQueue.name}")
    public void updateOldMovements(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        System.out.println("\n\n[R] Received transactionUpdates < " + msg + " >");

        try {
            RabbitTransactionBin movementBin = objectMapper.readValue(msg, RabbitTransactionBin.class);
            this.portfolioHistoryService.updateOldMovements(movementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}