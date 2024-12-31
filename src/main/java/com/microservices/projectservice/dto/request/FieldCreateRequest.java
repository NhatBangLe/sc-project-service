package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record FieldCreateRequest(
        @NotBlank(message = "fieldName cannot be null/blank.")
        @Size(max = 100, message = "fieldName cannot be longer than 100 characters.")
        String fieldName,
        @NotNull(message = "numberOrder cannot be null.")
        @Min(value = 0, message = "Invalid numberOrder (cannot be less than 0).")
        Integer numberOrder
) implements Serializable {
}
