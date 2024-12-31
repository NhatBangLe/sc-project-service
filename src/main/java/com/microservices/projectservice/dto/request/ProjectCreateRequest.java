package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record ProjectCreateRequest(
        String thumbnailId,
        @NotBlank(message = "name cannot be null/blank.")
        @Size(max = 100, message = "name cannot be longer than 100 characters.")
        String name,
        @Size(max = 255, message = "description cannot be longer than 255 characters.")
        String description,
        LocalDate startDate,
        LocalDate endDate,
        @NotBlank(message = "ownerId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "ownerId length must be 36 characters.")
        String ownerId,
        List<@NotBlank(message = "memberId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "memberId length must be 36 characters.") String> memberIds
) implements Serializable {
}
