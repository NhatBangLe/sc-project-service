package com.microservices.projectservice.dto;

import java.io.Serializable;

public record FormUpdateRequest(String title,
                                String description) implements Serializable {
}
