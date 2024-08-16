package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public record FieldCreateRequest(
        @NotBlank(message = "Field name cannot be null/blank when creating a field.")
        String fieldName,
        @Value(value = "0")
        Integer numberOrder
) implements Serializable {
}
