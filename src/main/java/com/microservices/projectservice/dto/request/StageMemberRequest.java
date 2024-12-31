package com.microservices.projectservice.dto.request;

import com.microservices.projectservice.constant.MemberOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record StageMemberRequest(
        @NotBlank(message = "memberId cannot be null/blank.")
        @Size(min = 36, max = 36, message = "memberId length must be 36 characters.")
        String memberId,
        @NotNull(message = "operator cannot be null.")
        MemberOperator operator
) implements Serializable {
}
