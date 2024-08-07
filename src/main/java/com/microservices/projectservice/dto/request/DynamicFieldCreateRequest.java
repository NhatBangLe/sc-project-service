package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record DynamicFieldCreateRequest(String name,
                                        String value,
                                        Integer numberOrder) implements Serializable {
}