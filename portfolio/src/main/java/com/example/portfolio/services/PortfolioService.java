package com.example.portfolio.services;

import java.util.List;

import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.models.bin.PutPortfolioNameBin;
import com.example.portfolio.models.bin.PutUserPrivacyBin;

public interface PortfolioService {

    Portfolio createPotfolio(PostPortfolioBin postPortfolioBin);

    Portfolio getPortfolio(long id);

    void deletePortfolio(long id);

    Portfolio updatePortfolioName(PutPortfolioNameBin putPortfolioNameBin);

    void updatePortfolioPrivacy(PutUserPrivacyBin putUserPrivacyBin);

    List<Portfolio> getPortfolioByUserId(String userId);

    void deletePortfolioByUserId(String userId);

}
