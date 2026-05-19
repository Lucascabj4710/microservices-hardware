package com.lucas.microservice.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
        return routeLocatorBuilder.routes()
                .route("product-service", r -> r
                        .path("/product/**")
                        .uri("lb://MICROSERVICE-PRODUCT")
                )
                .route("order-service", r -> r
                        .path("/order/**")
                        .uri("lb://MICROSERVICE-ORDER")
                )
                .build();
    }

}
