package com.example.transaction;

import java.util.Optional;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PublicEndpoint publicEndpoint = handlerMethod.getMethodAnnotation(PublicEndpoint.class);

            if (publicEndpoint == null) {
                String isAuthenticated = Optional.ofNullable(request.getHeader("X-Is-Authenticated")).orElse("");
                if (!"TRUE".equals(isAuthenticated.toUpperCase())) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                    return false;
                }
            }
        }
        return true;
    }
}
