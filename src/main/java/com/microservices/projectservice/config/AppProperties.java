package com.microservices.projectservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("app")
public class AppProperties {

    private String apiVersion;
    private String apiDocsServer;
    private String userServiceId;
    private String fileServiceId;

}
