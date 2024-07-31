package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record FormUpdateRequest(String title,
                                String description) implements Serializable {
}
