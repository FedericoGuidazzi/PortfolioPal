package controllers;

import models.GetPortfolioHistoryBin;
import models.PortfolioHistory;
import models.enums.DurationIntervalEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.PortfolioHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio-history")
public class PortfolioHistoryController {
    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

    @GetMapping("/{portfolioID}")
    public List<PortfolioHistory> getPortfolioHistory(
            @PathVariable long portfolioID,
            @RequestParam(required = false, defaultValue = "1S") String duration
    ) {
        GetPortfolioHistoryBin bin = GetPortfolioHistoryBin.builder()
                .portfolioId(portfolioID)
                .durationIntervalEnum(DurationIntervalEnum.fromValue(duration))
                .build();
        return portfolioHistoryService.getPortfolioHistory(bin);
    }

    @GetMapping("/ranking")
    public List<PortfolioHistory> getRanking(
    ) {
        return portfolioHistoryService.getRanking();
    }

    @PostMapping("/insertPortfolio")
    public void insertPortfolioInfo(
            @RequestParam(required = true) Long id
    ) {
        portfolioHistoryService.insertNewPortfolio(id);
    }

    @PostMapping("/deletePortfolio")
    public void deletePortfolioInfo(
            @RequestParam(required = true) Long id
    ) {
        portfolioHistoryService.deletePortfolio(id);
    }
}
