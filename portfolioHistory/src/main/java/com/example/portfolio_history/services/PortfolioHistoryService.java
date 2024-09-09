package com.example.portfolio_history.services;



import com.example.portfolio_history.models.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.MovementBin;
import com.example.portfolio_history.models.PortfolioHistory;

import java.util.List;

public interface PortfolioHistoryService {
    void insertIntradayMovement(MovementBin movementBin);

    void updateOldMovements(MovementBin movementBin);

    void insertNewDay();

    List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin);

}
