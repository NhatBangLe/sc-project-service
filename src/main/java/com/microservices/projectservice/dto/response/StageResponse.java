package com.microservices.projectservice.dto.response;

import java.io.Serializable;
import java.time.LocalDate;

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
                            String projectOwnerId) implements Serializable {
  }