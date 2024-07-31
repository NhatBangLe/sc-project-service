package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record FormCreateRequest(String title,
                                String description,
                                String projectOwnerId) implements Serializable {
}
