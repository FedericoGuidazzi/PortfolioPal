package com.example.portfolio.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.models.bin.PutPortfolioNameBin;
import com.example.portfolio.services.PortfolioService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @SneakyThrows
    @PostMapping("/create/{userId}")
    public ResponseEntity<Portfolio> createPortfolio(@PathVariable String userId, @RequestBody String name) {
        return ResponseEntity.ok(portfolioService.createPortfolio(
                PostPortfolioBin.builder()
                        .name(name)
                        .userId(userId)
                        .build()));
    }

    @SneakyThrows
    @GetMapping("/get/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id));
    }

    @SneakyThrows
    @GetMapping("/get/user/{userId}")
    public ResponseEntity<Portfolio> getPortfolioByUserId(@PathVariable String userId) {
        return portfolioService.getPortfolioByUserId(userId)
                .stream()
                .findFirst()
                .map(ResponseEntity::ok).orElse(null);
    }

    @SneakyThrows
    @PostMapping("/update/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable long id, @RequestBody String name) {
        return ResponseEntity.ok(
                portfolioService.updatePortfolioName(
                        PutPortfolioNameBin.builder()
                                .id(id)
                                .name(name)
                                .build()));
    }

}