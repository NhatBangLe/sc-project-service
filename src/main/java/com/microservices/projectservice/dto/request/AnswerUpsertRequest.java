package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record AnswerUpsertRequest(
        @NotNull(message = "Answer value cannot be null.")
        @Size(max = 255, message = "value cannot be longer than 255 characters.")
        String value,
        @NotBlank(message = "Field ID cannot be null/blank.")
        @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
        String fieldId
) implements Serializable {
}