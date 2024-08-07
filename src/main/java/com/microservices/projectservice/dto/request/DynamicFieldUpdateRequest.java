package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public record DynamicFieldUpdateRequest(
        @NotBlank(message = "Dynamic field name cannot be null/blank when updating a field.")
        String name,
        @Value(value = "")
        String value,
        @Value(value = "0")
        Integer numberOrder
) implements Serializable {
}