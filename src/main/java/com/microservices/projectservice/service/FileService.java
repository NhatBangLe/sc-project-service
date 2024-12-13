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
public class FileService {

    private RestClient client;

    @Autowired
    public void setClient(RestClient.Builder builder,
                          @Value("${FILE_SERVICE_INSTANCE_ID}") String fileServiceId) {
        this.client = builder
                .baseUrl("http://" + fileServiceId + "/api/file")
                .build();
    }

    public ResponseEntity<String> deleteFile(String fileId) throws HttpClientErrorException {
        try {
            return client.delete()
                    .uri("/{fileId}" + fileId)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException exception) {
            log.warn(exception.getMessage(), exception);
            return ResponseEntity
                    .status(exception.getStatusCode())
                    .body(exception.getMessage());
        }
    }

}
