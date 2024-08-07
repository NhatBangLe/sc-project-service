package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record DynamicFieldUpdateRequest(String dynamicFieldId,
                                        String name,
                                        String value,
                                        Integer numberOrder) implements Serializable {
}