package com.microservices.projectservice.mapper;

import com.microservices.projectservice.dto.response.StageResponse;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.entity.User;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class StageMapper implements IMapper<Stage, StageResponse> {

    @Override
    public StageResponse toResponse(Stage entity) {
        Set<User> members = Objects.requireNonNullElse(entity.getMembers(), Collections.emptySet());
        var memberIds = members
                .parallelStream()
                .map(User::getId)
                .toList();
        var startDate = entity.getStartDate();
        var endDate = entity.getEndDate();
        return new StageResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                startDate != null ? startDate.toString() : null,
                endDate != null ? endDate.toString() : null,
                entity.getForm().getId(),
                entity.getCreatedAt().getTime(),
                entity.getProjectOwner().getId(),
                memberIds
        );
    }

}
