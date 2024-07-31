package com.microservices.projectservice.dto.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record ProjectCreateRequest(String name,
                                   String description,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   String ownerId,
                                   List<String> memberIds) implements Serializable {
}
