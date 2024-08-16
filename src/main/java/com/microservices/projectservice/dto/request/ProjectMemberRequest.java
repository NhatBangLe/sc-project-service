package com.microservices.projectservice.dto.request;

import com.microservices.projectservice.constant.ProjectMemberOperator;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record ProjectMemberRequest(
        @NotBlank(message = "Member ID cannot be null/blank.")
        String memberId,
        ProjectMemberOperator operator
) implements Serializable {
}
