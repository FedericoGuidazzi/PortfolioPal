package com.example.portfolio_history.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.portfolio_history.custom_exceptions.CustomException;
import com.example.portfolio_history.models.Portfolio;
import com.example.portfolio_history.models.PortfolioInfo;
import com.example.portfolio_history.models.bin.PostPortfolioBin;
import com.example.portfolio_history.models.bin.PutPortfolioNameBin;
import com.example.portfolio_history.models.bin.PutUserPrivacyBin;
import com.example.portfolio_history.models.entities.PortfolioEntity;
import com.example.portfolio_history.models.entities.PortfolioHistoryEntity;
import com.example.portfolio_history.repositories.PortfolioHistoryRepository;
import com.example.portfolio_history.repositories.PortfolioRepository;
import com.example.portfolio_history.services.PortfolioService;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioHistoryRepository historyRepository;

    @Override
    public Portfolio createPortfolio(PostPortfolioBin postPortfolioBin) throws CustomException {
        List<PortfolioEntity> portfolioList = portfolioRepository
                .findAllByUserId(postPortfolioBin.getUserId());

        // check if the user already has a portfolio
        if (!portfolioList.isEmpty()) {
            throw new CustomException("User already has a portfolio");
        }

        // save the portfolio
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .name(postPortfolioBin.getName())
                .userId(postPortfolioBin.getUserId())
                .build();
        portfolioRepository.save(portfolioEntity);

        return this.fromEntityToObject(portfolioEntity);
    }

    @Override
    public Portfolio updatePortfolioName(PutPortfolioNameBin putPortfolioNameBin) throws CustomException {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(putPortfolioNameBin.getId())
                .orElseThrow(() -> new CustomException("Portfolio not found"));

        portfolioEntity.setName(putPortfolioNameBin.getName());
        portfolioRepository.save(portfolioEntity);

        return this.fromEntityToObject(portfolioEntity);
    }

    @Override
    public List<Portfolio> getPortfolioByUserId(String userId) throws CustomException {
        List<PortfolioEntity> portfolioList = portfolioRepository.findAllByUserId(userId);

        if (portfolioList.isEmpty()) {
            throw new CustomException("User does not have a portfolio");
        }

        return portfolioList.stream()
                .map(this::fromEntityToObject)
                .toList();
    }

    @Override
    public void deletePortfolioByUserId(String userId) {
        List<PortfolioEntity> portfolioList = portfolioRepository.findAllByUserId(userId);

        for (PortfolioEntity portfolioEntity : portfolioList) {
            portfolioRepository.delete(portfolioEntity);
        }
    }

    @Override
    public Portfolio getPortfolio(long id, boolean isRequesterOwner) throws CustomException {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new CustomException("Portfolio not found"));
        if (isRequesterOwner || portfolioEntity.isSherable()) {
            return this.fromEntityToObject(portfolioEntity);
        } else {
            throw new CustomException("Portfolio is not sharable");
        }

    }

    @Override
    public void deletePortfolio(long id) {
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(id)
                .build();
        portfolioRepository.delete(portfolioEntity);

    }

    @Override
    public void updatePortfolioPrivacy(PutUserPrivacyBin putUserPrivacyBin) {
        List<PortfolioEntity> portfolioList = portfolioRepository.findAllByUserId(putUserPrivacyBin.getUserID());

        for (PortfolioEntity portfolioEntity : portfolioList) {
            portfolioEntity.setSherable(putUserPrivacyBin.isSherable());
            portfolioRepository.save(portfolioEntity);
        }
    }

    @Override
    public List<PortfolioInfo> getRanking() {
        // Get all portfolios order by percentageValue
        List<PortfolioHistoryEntity> allPortfolios = historyRepository.findAllOrderByPercentageValueDesc();

        // Remove all the portfolios that are not sharable and select the top 10
        return allPortfolios.stream()
         .filter(this::isPortfolioSharable)
         .limit(10)
         .map(e-> PortfolioInfo.builder()
                 .idPortfolio(e.getPortfolioId())
                 .portfolioName(portfolioRepository.findById(e.getPortfolioId())
                 .map(PortfolioEntity::getName).orElse(Strings.EMPTY))
                 .percentageValue(e.getPercentageValue()).build())
         .collect(Collectors.toList());
    }

    private boolean isPortfolioSharable(PortfolioHistoryEntity item) {
        return portfolioRepository.findById(item.getId())
                .map(PortfolioEntity::isSherable)
                .orElse(false);
    }

    private Portfolio fromEntityToObject(PortfolioEntity entity) {
        return Portfolio.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isSherable(entity.isSherable())
                .userId(entity.getUserId())
                .build();
    }

}
