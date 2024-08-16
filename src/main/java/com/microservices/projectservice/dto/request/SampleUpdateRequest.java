package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record SampleUpdateRequest(
        String attachmentId,
        String position
) implements Serializable {
}