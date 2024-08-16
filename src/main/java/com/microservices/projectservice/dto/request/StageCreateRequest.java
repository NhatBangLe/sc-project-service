package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDate;

public record StageCreateRequest(
        @NotBlank(message = "Stage name cannot be null/blank when creating a stage.")
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String formId,
        @NotBlank(message = "Project owner ID cannot be null/blank when creating a stage.")
        String projectOwnerId
) implements Serializable {
}
