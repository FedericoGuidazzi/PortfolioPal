package com.example.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import com.example.api_gateway.filters.FireBaseTokenFilter;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	@Autowired
	private FireBaseTokenFilter auTokenFilter;

	// @Autowired
	// private OwnershipPortfolioFilter ownershipPortfolioFilter;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/user/**")
						.filters(f -> f
								.filter(auTokenFilter)
								.rewritePath("/user/(?<segment>.*)", "/api/v1/user/${segment}"))
						.uri("lb://user"))
				.route(r -> r.path("/transaction/**")
						.filters(f -> f
								.rewritePath("/transaction/(?<segment>.*)", "/api/v1/transaction/${segment}"))
						.uri("lb://transaction"))
				.route(r -> r.path("/portfolio/**")
						.filters(f -> f
								.filter(auTokenFilter)
								.rewritePath("/portfolio/(?<segment>.*)", "/api/v1/portfolio/${segment}"))
						.uri("lb://portfolio-history"))
				.route(r -> r.path("/portfolio-history/**")
						.filters(f -> f
								.rewritePath("/portfolio-history/(?<segment>.*)",
										"/api/v1/portfolio-history/${segment}"))
						.uri("lb://portfolio-history"))
				.route(r -> r.path("/asset/**")
						.filters(f -> f
								.rewritePath("/asset/(?<segment>.*)", "/api/v1/asset/${segment}"))
						.uri("lb://asset"))
				.route(r -> r.path("/currency/**")
						.filters(f -> f
								.rewritePath("/currency/(?<segment>.*)", "/api/v1/currency/${segment}"))
						.uri("lb://asset"))
				.build();
	}

}
