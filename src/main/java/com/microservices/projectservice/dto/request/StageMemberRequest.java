package com.microservices.projectservice.dto.request;

import com.microservices.projectservice.constant.MemberOperator;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record StageMemberRequest(
        @NotBlank(message = "Member ID cannot be null/blank.")
        String memberId,
        MemberOperator operator
) implements Serializable {
}
