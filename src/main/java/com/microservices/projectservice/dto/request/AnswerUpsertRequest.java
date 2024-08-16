package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record AnswerUpsertRequest(
        @NotNull(message = "Answer value cannot be null when inserting/updating a answer.")
        String value,
        @NotBlank(message = "Field ID cannot be null/blank when inserting/updating a answer.")
        String fieldId
) implements Serializable {
}