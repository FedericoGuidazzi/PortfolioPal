package services;

import models.GetPortfolioHistoryBin;
import models.IntradayMovementBin;
import models.PortfolioHistory;
import models.UpdatePortfolioInfoBin;

import java.util.List;

public interface PortfolioHistoryService {
    void insertIntradayMovement(IntradayMovementBin intradayMovementBin);

    void insertNewDay();

    void insertNewPortfolio(Long id);

    void deletePortfolio(Long id);

    List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin);

    List<PortfolioHistory> getRanking();

    void updatePrivacySetting(UpdatePortfolioInfoBin inputBin);
}
