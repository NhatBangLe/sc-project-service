package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record FormCreateRequest(
        @NotBlank(message = "title cannot be null/blank.")
        @Size(max = 100, message = "title cannot be longer than 100 characters.")
        String title,
        @Size(max = 255, message = "description cannot be longer than 255 characters.")
        String description,
        @NotBlank(message = "projectOwnerId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "projectOwnerId length must be 36 characters.")
        String projectOwnerId
) implements Serializable {
}
