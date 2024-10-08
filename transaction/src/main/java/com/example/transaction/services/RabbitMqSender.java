package com.example.transaction.services;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RabbitMqSender {
    @Autowired
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private final Queue transactionQueue;

    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue transactionQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.transactionQueue = transactionQueue;
    }

    public void sendTransactionUpdate(GetTransactionByDateBin transactionBin) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try {
            String jsonUser = objectMapper.writeValueAsString(transactionBin);
            this.rabbitTemplate.convertAndSend(this.transactionQueue.getName(), jsonUser);
            System.out.println("\n\n[S] Sent " + transactionBin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
