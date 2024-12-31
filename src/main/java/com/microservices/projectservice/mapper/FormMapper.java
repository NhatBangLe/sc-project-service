package com.microservices.projectservice.mapper;

import com.microservices.projectservice.dto.response.FormResponse;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Stage;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class FormMapper implements IMapper<Form, FormResponse> {

    @Override
    public FormResponse toResponse(Form entity) {
        Set<Stage> usageStages = Objects.requireNonNullElse(entity.getUsageStages(), Collections.emptySet());
        var usageStageIds = usageStages
                .stream()
                .map(Stage::getId)
                .toList();
        return new FormResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getProjectOwner().getId(),
                entity.getCreatedAt().getTime(),
                usageStageIds
        );
    }

}
