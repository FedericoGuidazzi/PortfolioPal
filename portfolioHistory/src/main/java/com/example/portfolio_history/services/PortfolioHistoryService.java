package com.example.portfolio_history.services;

import java.util.List;

import com.example.portfolio_history.models.PortfolioHistory;
import com.example.portfolio_history.models.bin.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.bin.RabbitTransactionBin;

public interface PortfolioHistoryService {

    void updateOldMovements(RabbitTransactionBin movementBin);

    void insertNewDay();

    List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin);

}
