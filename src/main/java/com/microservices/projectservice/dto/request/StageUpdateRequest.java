package com.microservices.projectservice.dto.request;

import java.io.Serializable;
import java.time.LocalDate;

public record StageUpdateRequest(String name,
                                 String description,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String formId) implements Serializable {
}
