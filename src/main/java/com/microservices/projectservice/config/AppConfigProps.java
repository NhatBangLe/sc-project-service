package com.microservices.projectservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app")
public class AppConfigProps {

    private String apiVersion;
    private String apiDocsServer;
    private String userServiceUrl;
    private String fileServiceUrl;

}
