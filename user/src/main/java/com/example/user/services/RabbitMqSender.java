package com.example.user.services;

import com.example.user.models.bin.PutUserPrivacyBin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSender {

    @Autowired
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private final Queue userQueue;

    public RabbitMqSender(RabbitTemplate rabbitTemplate, Queue userQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.userQueue = userQueue;
    }

    public void send(PutUserPrivacyBin privacyBin) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String jsonUser = objectMapper.writeValueAsString(privacyBin);
            this.rabbitTemplate.convertAndSend(this.userQueue.getName(), jsonUser);
            System.out.println("\n\n[S] Sent " + privacyBin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
