package com.example.portfolio_history.services;

import java.util.List;

import com.example.portfolio_history.custom_exceptions.CustomException;
import com.example.portfolio_history.models.Portfolio;
import com.example.portfolio_history.models.PortfolioInfo;
import com.example.portfolio_history.models.bin.PostPortfolioBin;
import com.example.portfolio_history.models.bin.PutPortfolioNameBin;
import com.example.portfolio_history.models.bin.PutUserPrivacyBin;


public interface PortfolioService {

    Portfolio createPortfolio(PostPortfolioBin postPortfolioBin) throws CustomException;

    Portfolio getPortfolio(long id, boolean isRequesterOwner) throws CustomException;

    void deletePortfolio(long id);

    Portfolio updatePortfolioName(PutPortfolioNameBin putPortfolioNameBin) throws CustomException;

    void updatePortfolioPrivacy(PutUserPrivacyBin putUserPrivacyBin);

    List<Portfolio> getPortfolioByUserId(String userId) throws CustomException;

    void deletePortfolioByUserId(String userId);

    List<PortfolioInfo> getRanking();

}
