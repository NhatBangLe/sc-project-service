package com.microservices.projectservice.dto.request;

import java.io.Serializable;

public record AnswerUpdateRequest(String value) implements Serializable {
}