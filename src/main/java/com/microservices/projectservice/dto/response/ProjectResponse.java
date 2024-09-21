package com.microservices.projectservice.dto.response;

import com.microservices.projectservice.entity.Project;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Project}
 */
public record ProjectResponse(
        String id,
        String thumbnailId,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String ownerId,
        List<String> memberIds
) implements Serializable {
}