package com.microservices.projectservice.dto.response;

import java.io.Serializable;

/**
 * DTO for {@link com.microservices.projectservice.entity.Field}
 */
public record FieldResponse(String id,
                            Integer numberOrder,
                            String content,
                            String formId) implements Serializable {
}