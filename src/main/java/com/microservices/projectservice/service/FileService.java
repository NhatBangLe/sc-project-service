package com.microservices.projectservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    public ResponseEntity<String> deleteFile(String fileId) {
        return client.delete()
                .uri(fileId)
                .retrieve()
                .toEntity(String.class);
    }

}
