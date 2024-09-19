package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record ProjectCreateRequest(
        String thumbnailId,
        @NotBlank(message = "Project name cannot be null/blank when creating a project.")
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        @NotBlank(message = "Owner ID cannot be null/blank when creating a project.")
        String ownerId,
        List<String> memberIds
) implements Serializable {
}
