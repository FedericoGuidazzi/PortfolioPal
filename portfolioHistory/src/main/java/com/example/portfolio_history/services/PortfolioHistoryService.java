package com.example.portfolio_history.services;



import com.example.portfolio_history.models.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.MovementBin;
import com.example.portfolio_history.models.PortfolioHistory;
import com.example.portfolio_history.models.UpdatePortfolioInfoBin;

import java.util.List;

public interface PortfolioHistoryService {
    void insertIntradayMovement(MovementBin movementBin);

    void updateOldMovements(MovementBin movementBin);

    void insertNewDay();

    void insertNewPortfolio(Long id);

    void deletePortfolio(Long id);

    List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin);

    List<PortfolioHistory> getRanking();

    void updatePrivacySetting(UpdatePortfolioInfoBin inputBin);
}
