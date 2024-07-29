package com.microservices.projectservice.dto;

import java.io.Serializable;

public record FormCreateRequest(String title,
                                String description,
                                String projectOwnerId) implements Serializable {
}
