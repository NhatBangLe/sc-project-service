package com.microservices.projectservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

public record SampleCreateRequest(
        @NotBlank(message = "Attachment ID cannot be null/blank.")
        String attachmentId,
        String position,
        @NotBlank(message = "Stage ID cannot be null/blank.")
        String stageId,
        Set<AnswerUpsertRequest> answers,
        Set<DynamicFieldCreateRequest> dynamicFields
) implements Serializable {
}