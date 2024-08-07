package com.microservices.projectservice.dto.response;

import com.microservices.projectservice.entity.answer.Answer;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * DTO for {@link com.microservices.projectservice.entity.Sample}
 */
public record SampleResponse(@NotNull(message = "Sample ID cannot be null in any response") String id,
                             String position,
                             @NotNull(message = "Created date time of sample cannot be null in any response")
                             Timestamp createdTimestamp,
                             @NotNull(message = "Project owner ID cannot be null in any response")
                             String projectOwnerId,
                             String stageId,
                             List<AnswerResponse> answers,
                             List<DynamicFieldResponse> dynamicFields) implements Serializable {
    /**
     * DTO for {@link Answer}
     */
    public record AnswerResponse(String value, FieldResponse field) implements Serializable {
    }

    /**
     * DTO for {@link com.microservices.projectservice.entity.DynamicField}
     */
    public record DynamicFieldResponse(String id, String name, String value,
                                       Integer numberOrder) implements Serializable {
    }
}