package com.example.portfolio_history.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio_history.models.PortfolioHistory;
import com.example.portfolio_history.models.bin.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.enums.DurationIntervalEnum;
import com.example.portfolio_history.services.PortfolioHistoryService;

@RestController
@RequestMapping("/api/v1/portfolio-history")
public class PortfolioHistoryController {
    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

    @GetMapping("/{portfolioId}")
    public List<PortfolioHistory> getPortfolioHistory(
            @PathVariable long portfolioId,
            @RequestParam(required = false) String duration) {

        if (duration == null || duration.isBlank()) {
            return portfolioHistoryService
                    .getPortfolioHistory(GetPortfolioHistoryBin.builder().portfolioId(portfolioId).build());
        }

        GetPortfolioHistoryBin bin = GetPortfolioHistoryBin.builder()
                .portfolioId(portfolioId)
                .durationIntervalEnum(DurationIntervalEnum.fromValue(duration))
                .build();
        return portfolioHistoryService.getPortfolioHistory(bin);
    }

}
