package com.example.employment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employmentSystemOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Graduate Employment Management System API")
                .description("Round 9 backend delivery-prep API documentation")
                .version("v1"));
    }
}
