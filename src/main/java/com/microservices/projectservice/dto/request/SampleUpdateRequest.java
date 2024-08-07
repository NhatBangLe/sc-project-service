package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record SampleUpdateRequest(String position) implements Serializable {
}