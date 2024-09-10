package com.example.portfolio_history.controllers;

import com.example.portfolio_history.models.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.PortfolioHistory;
import com.example.portfolio_history.models.enums.DurationIntervalEnum;
import com.example.portfolio_history.services.PortfolioHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio-history")
public class PortfolioHistoryController {
    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

    @GetMapping("/{portfolioId}")
    public List<PortfolioHistory> getPortfolioHistory(
            @PathVariable long portfolioId,
            @RequestParam(required = false, defaultValue = "1S") String duration) {

        GetPortfolioHistoryBin bin = GetPortfolioHistoryBin.builder()
                .portfolioId(portfolioId)
                .durationIntervalEnum(DurationIntervalEnum.fromValue(duration))
                .build();
        return portfolioHistoryService.getPortfolioHistory(bin);
    }

}
