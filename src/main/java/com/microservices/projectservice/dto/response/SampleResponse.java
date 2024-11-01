package com.microservices.projectservice.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.microservices.projectservice.entity.Sample}
 */
public record SampleResponse(
        String id,
        String attachmentId,
        String position,
        Long createdAt,
        String projectOwnerId,
        String stageId,
        List<AnswerResponse> answers,
        List<DynamicFieldResponse> dynamicFields
) implements Serializable {
    /**
     * DTO for {@link com.microservices.projectservice.entity.answer.Answer}
     */
    public record AnswerResponse(
            String value,
            FieldResponse field
    ) implements Serializable {
    }

    /**
     * DTO for {@link com.microservices.projectservice.entity.DynamicField}
     */
    public record DynamicFieldResponse(
            String id,
            String name,
            String value,
            Integer numberOrder,
            Long createdAt
    ) implements Serializable {
    }
}