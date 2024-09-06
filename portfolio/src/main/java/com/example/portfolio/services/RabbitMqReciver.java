package com.example.portfolio.services;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.portfolio.models.bin.PutUserPrivacyBin;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableRabbit
public class RabbitMqReciver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PortfolioService portfolioService;

    @RabbitListener(queues = "privacyUpdates")
    public void updatePrivacy(String msg) {
        System.out.println("\n\n[R] Received < " + msg + " >");

        try {
            PutUserPrivacyBin privacyBin = objectMapper.readValue(msg, PutUserPrivacyBin.class);
            this.portfolioService.updatePortfolioPrivacy(privacyBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
