package com.example.portfolio.services;

import java.util.List;

import com.example.portfolio.custom_exceptions.CustomException;
import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.models.bin.PutPortfolioNameBin;
import com.example.portfolio.models.bin.PutUserPrivacyBin;

public interface PortfolioService {

    Portfolio createPotfolio(PostPortfolioBin postPortfolioBin) throws CustomException;

    Portfolio getPortfolio(long id) throws CustomException;

    void deletePortfolio(long id);

    Portfolio updatePortfolioName(PutPortfolioNameBin putPortfolioNameBin) throws CustomException;

    void updatePortfolioPrivacy(PutUserPrivacyBin putUserPrivacyBin);

    List<Portfolio> getPortfolioByUserId(String userId) throws CustomException;

    void deletePortfolioByUserId(String userId);

}
