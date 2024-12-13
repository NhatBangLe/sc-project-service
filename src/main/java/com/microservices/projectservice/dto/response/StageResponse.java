package com.microservices.projectservice.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.microservices.projectservice.entity.Stage}
 */
public record StageResponse(String id,
                            String name,
                            String description,
                            LocalDate startDate,
                            LocalDate endDate,
                            String formId,
                            Long createdAt,
                            String projectOwnerId,
                            List<String> memberIds
) implements Serializable {
}