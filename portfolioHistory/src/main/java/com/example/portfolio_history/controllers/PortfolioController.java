package com.example.portfolio_history.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio_history.PublicEndpoint;
import com.example.portfolio_history.models.Portfolio;
import com.example.portfolio_history.models.PortfolioInfo;
import com.example.portfolio_history.models.PostPortfolioDto;
import com.example.portfolio_history.models.bin.PostPortfolioBin;
import com.example.portfolio_history.models.bin.PutPortfolioNameBin;
import com.example.portfolio_history.services.PortfolioService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Portfolio> createPortfolio(@RequestHeader("X-Authenticated-UserId") String userId,
            @RequestBody PostPortfolioDto postPortfolioDto) {
        return ResponseEntity.ok(portfolioService.createPortfolio(
                PostPortfolioBin.builder()
                        .name(postPortfolioDto.getName())
                        .userId(userId)
                        .isSharable(postPortfolioDto.isShare())
                        .build()));
    }

    @SneakyThrows
    @GetMapping("/get/personal/{id}")
    public ResponseEntity<Portfolio> getPersonalPortfolioById(@PathVariable long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id, true));
    }

    @PublicEndpoint
    @SneakyThrows
    @GetMapping("/get/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id, false));
    }

    @SneakyThrows
    @GetMapping("/get/user")
    public ResponseEntity<Portfolio> getPortfolioByUserId(@RequestHeader("X-Authenticated-UserId") String userId) {
        return portfolioService.getPortfolioByUserId(userId)
                .stream()
                .findFirst()
                .map(ResponseEntity::ok).orElse(null);
    }

    @SneakyThrows
    @PostMapping("/update/name/{id}")
    public ResponseEntity<Portfolio> updatePortfolioName(@PathVariable long id, @RequestBody String name) {
        return ResponseEntity.ok(
                portfolioService.updatePortfolioName(
                        PutPortfolioNameBin.builder()
                                .id(id)
                                .name(name)
                                .build()));
    }

    @PublicEndpoint
    @GetMapping("/ranking")
    public List<PortfolioInfo> getRanking() {
        return portfolioService.getRanking();
    }

}