package com.microservices.projectservice.dto.request;

import com.microservices.projectservice.constant.ProjectStatus;

import java.io.Serializable;
import java.time.LocalDate;

public record ProjectUpdateRequest(
        String name,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate
) implements Serializable {
}
