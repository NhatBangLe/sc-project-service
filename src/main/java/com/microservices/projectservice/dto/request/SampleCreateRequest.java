package com.microservices.projectservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Set;

public record SampleCreateRequest(
        @NotBlank(message = "attachmentId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "attachmentId length must be 36 characters.")
        String attachmentId,
        String position,
        @NotBlank(message = "stageId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
        String stageId,
        Set<@Valid AnswerUpsertRequest> answers,
        Set<@Valid DynamicFieldCreateRequest> dynamicFields
) implements Serializable {
}