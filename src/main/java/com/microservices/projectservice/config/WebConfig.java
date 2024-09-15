package com.microservices.projectservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final DiscoveryClient discoveryClient;

    @Value("${GATEWAY_INSTANCE_ID}")
    private String gatewayId;

    public List<String> gatewayUrls() {
        var instances = discoveryClient.getInstances(gatewayId);
        if (instances.isEmpty()) return List.of("*");

        return instances.stream()
                .map(instance -> instance.getUri().toString())
                .toList();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var registeredMapping = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PATCH", "DELETE")
                .maxAge(3600);
        gatewayUrls().forEach(registeredMapping::allowedOrigins);
    }

}
