package com.microservices.projectservice.dto.request;

import java.io.Serializable;
import java.util.Set;

public record SampleCreateRequest(String position,
                                  String projectOwnerId,
                                  String stageId,
                                  Set<AnswerCreateRequest> answers,
                                  Set<DynamicFieldCreateRequest> dynamicFields) implements Serializable {
    public record AnswerCreateRequest(String value, String fieldId) implements Serializable {
    }

    public record DynamicFieldCreateRequest(String name, String value, Integer numberOrder) implements Serializable {
    }
}