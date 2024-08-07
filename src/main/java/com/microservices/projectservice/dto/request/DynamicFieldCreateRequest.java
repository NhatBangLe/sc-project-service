package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public record DynamicFieldCreateRequest(
        @NotBlank(message = "Dynamic field name cannot be null/blank when creating a field.")
        String name,
        @NotNull(message = "Dynamic field value cannot be null.")
        String value,
        @Value(value = "0")
        Integer numberOrder
) implements Serializable {
}