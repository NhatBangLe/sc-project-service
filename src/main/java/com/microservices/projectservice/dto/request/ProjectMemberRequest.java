package com.microservices.projectservice.dto.request;

import com.microservices.projectservice.constant.ProjectMemberOperator;

import java.io.Serializable;

public record ProjectMemberRequest(String memberId,
                                   ProjectMemberOperator operator) implements Serializable {
}
