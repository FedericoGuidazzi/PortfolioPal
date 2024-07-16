package com.example.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @GetMapping("/create")
    public ResponseEntity<User> create(@RequestHeader("uid") String id) {
        return ResponseEntity.ok(userService.addUser(id));
    }

    @SneakyThrows
    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestHeader("uid") String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/update-privacy")
    public void updateUserPrivacy(@RequestHeader("uid") String id, @RequestBody boolean sharePortfolio) {
        PutUserPrivacyBin privacyBin = PutUserPrivacyBin.builder()
                .userID(id)
                .sharePortfolio(sharePortfolio)
                .build();
        this.sender.send(privacyBin);
    }

    @SneakyThrows
    @PutMapping("/update-currency")
    public ResponseEntity<User> updateCurrency(@RequestHeader("uid") String id, @RequestBody String currency) {
        PutUserCurrencyBin currencyBin = PutUserCurrencyBin.builder()
                .userID(id)
                .currency(currency)
                .build();
        return ResponseEntity.ok(this.userService.updateCurrency(currencyBin));
    }

}
