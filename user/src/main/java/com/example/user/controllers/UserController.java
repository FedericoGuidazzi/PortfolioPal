package com.example.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.models.User;
import com.example.user.models.bin.PutUserCurrencyBin;
import com.example.user.models.bin.PutUserPrivacyBin;
import com.example.user.services.RabbitMqSender;
import com.example.user.services.UserService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private RabbitMqSender sender;

    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping("/create/{id}")
    public ResponseEntity<User> create(@PathVariable String id) {
        return ResponseEntity.ok(userService.addUser(id));
    }

    @SneakyThrows
    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/update_privacy/{id}")
    public void updateUserPrivacy(@PathVariable String id, @RequestBody boolean sharePortfolio) {
        PutUserPrivacyBin privacyBin = PutUserPrivacyBin.builder()
                .userID(id)
                .sharePortfolio(sharePortfolio)
                .build();
        this.sender.send(privacyBin);
    }

    @SneakyThrows
    @PutMapping("/update_currency/{id}")
    public ResponseEntity<User> updateCurrency(@PathVariable String id, @RequestBody String currency) {
        PutUserCurrencyBin currencyBin = PutUserCurrencyBin.builder()
                .userID(id)
                .currency(currency)
                .build();
        return ResponseEntity.ok(this.userService.updateCurrency(currencyBin));
    }

}
