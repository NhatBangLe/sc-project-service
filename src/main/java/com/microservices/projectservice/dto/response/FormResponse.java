package com.microservices.projectservice.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.microservices.projectservice.entity.Form}
 */
public record FormResponse(String id,
                           String title,
                           String description,
                           String projectOwnerId,
                           List<String> usageStageIds) implements Serializable {
}