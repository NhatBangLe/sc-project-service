package com.microservices.projectservice.dto.request;

import java.io.Serializable;
import java.time.LocalDate;

public record ProjectUpdateRequest(String name,
                                   String description,
                                   LocalDate startDate,
                                   LocalDate endDate) implements Serializable {
}
