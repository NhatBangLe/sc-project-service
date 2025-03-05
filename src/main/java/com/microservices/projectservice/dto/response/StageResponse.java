package com.microservices.projectservice.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.microservices.projectservice.entity.Stage}
 */
public record StageResponse(String id,
                            String name,
                            String description,
                            String startDate,
                            String endDate,
                            String formId,
                            Long createdAt,
                            String projectOwnerId,
                            List<String> memberIds
) implements Serializable {
}