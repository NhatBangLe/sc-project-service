package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record DynamicFieldCreateRequest(
        @NotBlank(message = "Dynamic field name cannot be null/blank when creating a field.")
        String name,
        @NotNull(message = "Dynamic field value cannot be null.")
        String value,
        Integer numberOrder
) implements Serializable {
}