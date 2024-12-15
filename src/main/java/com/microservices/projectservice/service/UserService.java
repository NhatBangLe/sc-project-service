package com.microservices.projectservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class UserService {

    private RestClient client;

    @Autowired
    public void setClient(RestClient.Builder builder,
                          @Value("${USER_SERVICE_INSTANCE_ID}") String userServiceId) {
        this.client = builder
                .baseUrl("http://" + userServiceId + "/api/v1/user")
                .build();
    }

    public ResponseEntity<?> getUser(String userId) {
        try {
            return client.get()
                    .uri("/{userId}", userId)
                    .retrieve()
                    .toEntity(Object.class);
        } catch (HttpClientErrorException exception) {
            log.warn(exception.getMessage(), exception);
            return ResponseEntity
                    .status(exception.getStatusCode())
                    .body(exception.getMessage());
        }
    }

}
