package com.microservices.projectservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocsConfiguration {

    @Bean
    public OpenAPI getOpenAPI() {
        var info = new Info()
                .title("Project Service")
                .version("1.0")
                .description(
                        "This is a set of services which is developed for sample collecting projects using by Expert. " +
                        "It supports to manage projects, forms, stages and samples."
                );

        var openApi = new OpenAPI();
        openApi.setInfo(info);

        return openApi;
    }

}
