package com.example.portfolio.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.services.PortfolioService;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Portfolio> createPortfolio(@PathVariable String userId, @RequestBody String name) {
        return ResponseEntity.ok(portfolioService.createPotfolio(
                PostPortfolioBin.builder()
                        .name(name)
                        .userId(userId)
                        .build()));
    }
}