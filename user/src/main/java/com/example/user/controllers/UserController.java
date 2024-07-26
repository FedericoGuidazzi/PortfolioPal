package com.example.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.PublicEndpoint;
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
    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestHeader("X-Authenticated-UserId") String id) {
        return ResponseEntity.ok(userService.addUser(id));
    }

    @PublicEndpoint
    @SneakyThrows
    @GetMapping("/get-name/{id}")
    public ResponseEntity<String> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id).getName());
    }

    @SneakyThrows
    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestHeader("X-Authenticated-UserId") String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/update-privacy")
    public void updateUserPrivacy(@RequestHeader("X-Authenticated-UserId") String id, @RequestBody boolean sharePortfolio) {
        PutUserPrivacyBin privacyBin = PutUserPrivacyBin.builder()
                .userID(id)
                .sharePortfolio(sharePortfolio)
                .build();
        this.sender.send(privacyBin);
    }

    @SneakyThrows
    @PutMapping("/update-currency")
    public ResponseEntity<User> updateCurrency(@RequestHeader("X-Authenticated-UserId") String id, @RequestBody String currency) {
        PutUserCurrencyBin currencyBin = PutUserCurrencyBin.builder()
                .userID(id)
                .currency(currency)
                .build();
        return ResponseEntity.ok(this.userService.updateCurrency(currencyBin));
    }

}
