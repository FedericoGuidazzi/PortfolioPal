package com.example.portfolio_history.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduler {

    @Autowired
    private PortfolioHistoryService portfolioHistoryService;

	@Scheduled(cron = "0 1 0 * * *")
	public void insertNewDay() {
        portfolioHistoryService.insertNewDay();
    }
}
