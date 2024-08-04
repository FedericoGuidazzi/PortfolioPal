package com.example.api_gateway.filters;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.enums.AuthToBoolean;
import com.example.api_gateway.models.Portfolio;

import reactor.core.publisher.Mono;

public class OwnershipPortfolioFilter implements GatewayFilter {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String uid = request.getHeaders().getFirst("X-Authenticated-UserId");
        boolean isAuth = AuthToBoolean.getBoolFromValue(request.getHeaders().getFirst("X-Is-Authenticated"));

        if (!isAuth) {
            return chain.filter(exchange);
        }

        if (uid != null) {
            // Check if user is owner of the portfolio

            // Get portfolio id from the path of the request
            String path = request.getPath().toString();
            String[] pathSegments = path.split("/");
            Long portfolioId = Long.parseLong(pathSegments[pathSegments.length - 1]);

            // Check if user is owner of the portfolio
            if (this.isOwner(portfolioId, uid)) {
                return chain.filter(exchange);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User unauthorized");
            }

        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User unauthorized");
    }

    private boolean isOwner(long portfolioId, String uid) {
        String url = discoveryClient.getInstances("portfolio-service").stream().findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Portfolio service not found"))
                .getUri().toString();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<Portfolio>> responseEntity = restTemplate.exchange(
                    url + "/get/user/" + uid,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Portfolio>>() {
                    });

            List<Portfolio> portfolios = Optional.ofNullable(responseEntity.getBody()).orElse(List.of());

            return portfolios.stream()
                    .anyMatch(portfolio -> portfolio.getId() == portfolioId);

        } catch (Exception e) {
            // throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            // "Portfolio service is not available");
        }

        return false;
    }

}
