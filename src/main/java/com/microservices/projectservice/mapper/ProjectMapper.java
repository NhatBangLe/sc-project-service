package com.microservices.projectservice.mapper;

import com.microservices.projectservice.dto.response.ProjectResponse;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.User;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class ProjectMapper implements IMapper<Project, ProjectResponse> {

    @Override
    public ProjectResponse toResponse(Project entity) {
        Set<User> members = Objects.requireNonNullElse(entity.getMembers(), Collections.emptySet());
        var memberIds = members
                .parallelStream()
                .map(User::getId)
                .toList();
        var startDate = entity.getStartDate();
        var endDate = entity.getEndDate();
        return new ProjectResponse(
                entity.getId(),
                entity.getThumbnailId(),
                entity.getName(),
                entity.getDescription(),
                startDate != null ? startDate.toString() : null,
                endDate != null ? endDate.toString() : null,
                entity.getCreatedAt().getTime(),
                entity.getOwner().getId(),
                memberIds
        );
    }

}
