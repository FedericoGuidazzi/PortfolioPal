package com.example.portfolio_history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PortfolioHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioHistoryApplication.class, args);
    }
}
