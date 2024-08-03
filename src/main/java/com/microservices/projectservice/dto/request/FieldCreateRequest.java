package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record FieldCreateRequest(String fieldName, Integer numberOrder) implements Serializable {
}
