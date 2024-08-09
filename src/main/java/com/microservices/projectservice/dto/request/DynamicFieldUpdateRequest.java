package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record DynamicFieldUpdateRequest(
        String name,
        String value,
        Integer numberOrder
) implements Serializable {
}