package com.microservices.projectservice.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@Validated
public class FileService {

    private final RestClient client;

    @Autowired
    public FileService(RestClient.Builder builder,
                       @Value("${app.file-service-id}") String fileServiceId) {
        this.client = builder
                .baseUrl("http://" + fileServiceId + "/api/file")
                .build();
    }

    @Nullable
    public Boolean checkFileExists(@NotBlank @Size(min = 36, max = 36) String fileId) {
        try {
            return client.get()
                    .uri("/information/{fileId}", fileId)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (HttpClientErrorException exception) {
            log.warn(exception.getMessage(), exception);
            return null;
        }
    }

    @Nullable
    public Boolean deleteFile(String fileId) {
        try {
            return client.delete()
                    .uri("/{fileId}", fileId)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (HttpClientErrorException exception) {
            log.warn(exception.getMessage(), exception);
            return null;
        }
    }

}
