package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record FieldUpdateRequest(
        String fieldName,
        Integer numberOrder
) implements Serializable {
}
