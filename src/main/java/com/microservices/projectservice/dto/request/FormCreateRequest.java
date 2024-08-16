package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record FormCreateRequest(
        @NotBlank(message = "Form title cannot be null/blank when creating a form.")
        String title,
        String description,
        @NotBlank(message = "Project owner ID cannot be null/blank when creating a form.")
        String projectOwnerId
) implements Serializable {
}
