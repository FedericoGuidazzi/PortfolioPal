package com.example.portfolio.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.portfolio.models.Portfolio;
import com.example.portfolio.models.bin.PostPortfolioBin;
import com.example.portfolio.models.bin.PutPortfolioNameBin;
import com.example.portfolio.models.entities.PortfolioEntity;
import com.example.portfolio.repositories.PortfolioRepository;

public class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePotfolio() {
        PostPortfolioBin postPortfolioBin = PostPortfolioBin.builder()
                .name("name")
                .userId("userId")
                .build();

        when(portfolioRepository.findAllByUserId("userId")).thenReturn(List.of());

        Portfolio portfolio = portfolioService.createPotfolio(postPortfolioBin);

        verify(portfolioRepository, times(1)).save(any(PortfolioEntity.class));
        assertEquals("name", portfolio.getName());
        assertEquals("userId", portfolio.getUserId());
    }

    @Test
    void testDeletePortfolio() {
        long portfolioId = 1L;
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(portfolioId)
                .userId("userId")
                .name("name")
                .build();

        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolioEntity));

        portfolioService.deletePortfolio(portfolioId);

        verify(portfolioRepository, times(1)).deleteById(portfolioId);
    }

    @Test
    void testDeletePortfolioByUserId() {
        String userId = "userId";
        long portfolioId = 1L;
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(portfolioId)
                .userId(userId)
                .name("name")
                .build();

        when(portfolioRepository.findAllByUserId(userId)).thenReturn(List.of(portfolioEntity));

        portfolioService.deletePortfolioByUserId(userId);

        verify(portfolioRepository, times(1)).delete(portfolioEntity);

    }

    @Test
    void testGetPortfolio() {
        long portfolioId = 1;
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(portfolioId)
                .userId("userId")
                .name("name")
                .build();

        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolioEntity));

        Portfolio portfolio = portfolioService.getPortfolio(portfolioId);

        assertEquals(portfolioId, portfolio.getId());
        assertEquals("userId", portfolio.getUserId());
        assertEquals("name", portfolio.getName());
    }

    @Test
    void testGetPortfolioByUserId() {
        String userId = "userId";
        long portfolioId = 1L;
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(portfolioId)
                .userId(userId)
                .name("name")
                .build();

        when(portfolioRepository.findAllByUserId(userId)).thenReturn(List.of(portfolioEntity));

        List<Portfolio> portfolios = portfolioService.getPortfolioByUserId(userId);

        assertEquals(1, portfolios.size());
        Portfolio portfolio = portfolios.get(0);
        assertEquals(portfolioId, portfolio.getId());
        assertEquals(userId, portfolio.getUserId());
        assertEquals("name", portfolio.getName());
    }

    @Test
    void testUpdatePortfolioName() {
        long portfolioId = 1L;
        String newName = "newName";
        String userId = "userId";
        PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                .id(portfolioId)
                .userId(userId)
                .name("name")
                .build();

        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolioEntity));

        Portfolio portfolio = portfolioService.updatePortfolioName(PutPortfolioNameBin.builder()
                .id(portfolioId)
                .name(newName)
                .build());

        verify(portfolioRepository, times(1)).save(portfolioEntity);
        assertEquals(newName, portfolio.getName());
    }
}
