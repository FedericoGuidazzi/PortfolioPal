package com.example.user.services;

import com.example.user.customExceptions.UserNotFoundException;
import com.example.user.models.User;
import com.example.user.models.bin.PutUserPrivacyBin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableRabbit
public class RabbitMqFirestoreReciver {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    FirebaseUserServiceImpl userService;

    @RabbitListener(queues = "privacyUpdates")
    public void updatePrivacy(String msg) {
        System.out.println("\n\n[R] Received < " + msg + " >" + " on user service");

        try {
            PutUserPrivacyBin privacyBin = objectMapper.readValue(msg, PutUserPrivacyBin.class);

            Optional<User> user = Optional.ofNullable(this.userService.getUser(privacyBin.getUserID()));
            if (user.isPresent()) {
                user.get().setSharePortfolio(privacyBin.isSherable());
                this.userService.setUser(privacyBin.getUserID(), user.get());
            } else {
                throw new UserNotFoundException("User not found with id: " + privacyBin.getUserID());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
