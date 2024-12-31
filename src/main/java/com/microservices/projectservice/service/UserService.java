package com.microservices.projectservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class UserService {

    private final RestClient client;

    @Autowired
    public UserService(RestClient.Builder builder,
                       @Value("${app.user-service-id}") String userServiceId) {
        this.client = builder
                .baseUrl("http://" + userServiceId + "/api/v1/user")
                .build();
    }

    @Nullable
    public Boolean checkUserExists(String userId) {
        try {
            return client.get()
                    .uri("/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .isSameCodeAs(HttpStatus.OK);
        } catch (HttpClientErrorException exception) {
            log.warn(exception.getMessage(), exception);
            return null;
        }
    }

}
