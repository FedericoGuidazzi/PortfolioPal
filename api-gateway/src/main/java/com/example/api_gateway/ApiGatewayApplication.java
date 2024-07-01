package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/user/**")
						.filters(f -> f.rewritePath("/user/(?<segment>.*)", "/api/v1/user/${segment}"))
						.uri("lb://user"))
				.route(r -> r.path("/transaction/**")
						.filters(f -> f.rewritePath("/transaction/(?<segment>.*)", "/api/v1/transaction/${segment}"))
						.uri("lb://transaction"))
				.build();
	}

}
