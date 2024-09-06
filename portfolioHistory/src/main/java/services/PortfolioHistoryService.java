package services;

import models.GetPortfolioHistoryBin;
import models.MovementBin;
import models.PortfolioHistory;
import models.UpdatePortfolioInfoBin;

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
