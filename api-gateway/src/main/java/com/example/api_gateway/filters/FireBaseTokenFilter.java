package com.example.api_gateway.filters;

import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import reactor.core.publisher.Mono;

@Component
public class FireBaseTokenFilter implements GatewayFilter {

    /**
     * Authenticating user via fireBase authorizer verify fireBase token and extract
     * Uid from token
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = "";
        ServerHttpRequest request = exchange.getRequest();

        // Extract token from header
        token = request.getHeaders().getFirst("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                // Verifies token to firebase server
                FirebaseToken decodedToken = Optional.ofNullable(FirebaseAuth.getInstance().verifyIdToken(token))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token!"));

                // Extract Uid
                String uid = decodedToken.getUid();

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-UserId", uid)
                        .header("X-Is-Authenticated", "TRUE")
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (FirebaseAuthException e) {
            }
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Is-Authenticated", "FALSE")
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());

    }
}
