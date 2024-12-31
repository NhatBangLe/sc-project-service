package com.microservices.projectservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiDocsConfiguration {

    @Value("${app.api-docs-server}")
    private String API_DOCS_SERVER;

    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Service")
                        .version("1.0.0")
                        .description(
                                "This is a set of services which is developed for sample collecting projects using by Expert. " +
                                "It supports to manage projects, forms, stages and samples."
                        ))
                .servers(List.of(new Server().url(API_DOCS_SERVER)));
    }

}
