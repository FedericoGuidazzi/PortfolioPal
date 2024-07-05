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

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/user/**")
						.filters(f -> f.rewritePath("/user/(?<segment>.*)", "/api/v1/user/${segment}")
								.filter(auTokenFilter))
						.uri("lb://user"))
				.route(r -> r.path("/transaction/**")
						.filters(f -> f.rewritePath("/transaction/(?<segment>.*)", "/api/v1/transaction/${segment}")
								.filter(auTokenFilter))
						.uri("lb://transaction"))
				.build();
	}

}
