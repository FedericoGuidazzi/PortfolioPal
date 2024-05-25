package com.example.portfolio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.models.bin.PutPortfolioNameBin;
import com.example.portfolio.models.bin.PutUserPrivacyBin;
import com.example.portfolio.models.entities.PortfolioEntity;
import com.example.portfolio.repositories.PortfolioRepository;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepository;

    @Override
    public Portfolio createPotfolio(PostPortfolioBin postPortfolioBin) {
        List<PortfolioEntity> portfolioList = portfolioRepository
                .findAllByUserId(postPortfolioBin.getUserId());

        // check if the user already has a portfolio
        if (!portfolioList.isEmpty()) {
            throw new UnsupportedOperationException("User already has a portfolio");
        }

        // save the portfolio
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .name(postPortfolioBin.getName())
                .userId(postPortfolioBin.getUserId())
                .build();
        portfolioRepository.save(portfolioEntity);

        return Portfolio.builder()
                .id(portfolioEntity.getId())
                .name(portfolioEntity.getName())
                .userId(portfolioEntity.getUserId())
                .build();
    }

    @Override
    public Portfolio updatePortfolioName(PutPortfolioNameBin putPortfolioNameBin) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(putPortfolioNameBin.getId())
                .orElseThrow(() -> new UnsupportedOperationException("Portfolio not found"));

        portfolioEntity.setName(putPortfolioNameBin.getName());
        portfolioRepository.save(portfolioEntity);

        return Portfolio.builder()
                .id(portfolioEntity.getId())
                .name(portfolioEntity.getName())
                .userId(portfolioEntity.getUserId())
                .build();
    }

    @Override
    public List<Portfolio> getPortfolioByUserId(String userId) {
        List<PortfolioEntity> portfolioList = portfolioRepository.findAllByUserId(userId);

        if (portfolioList.isEmpty()) {
            throw new UnsupportedOperationException("User does not have a portfolio");
        }

        return portfolioList.stream()
                .map(portfolioEntity -> Portfolio.builder()
                        .id(portfolioEntity.getId())
                        .name(portfolioEntity.getName())
                        .userId(portfolioEntity.getUserId())
                        .build())
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
    public Portfolio getPortfolio(long id) {
        PortfolioEntity portfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new UnsupportedOperationException("Portfolio not found"));

        return Portfolio.builder()
                .id(portfolioEntity.getId())
                .name(portfolioEntity.getName())
                .userId(portfolioEntity.getUserId())
                .build();
    }

    @Override
    public void deletePortfolio(long id) {
        Optional<PortfolioEntity> portfolioEntity = portfolioRepository.findById(id);
        if (portfolioEntity.isPresent()) {
            portfolioRepository.delete(portfolioEntity.get());

        }

    }

    @Override
    public void updatePortfolioPrivacy(PutUserPrivacyBin putUserPrivacyBin) {
        List<PortfolioEntity> portfolioList = portfolioRepository.findAllByUserId(putUserPrivacyBin.getUserID());

        for (PortfolioEntity portfolioEntity : portfolioList) {
            portfolioEntity.setSharePortfolio(putUserPrivacyBin.isSharePortfolio());
            portfolioRepository.save(portfolioEntity);
        }
    }

}
