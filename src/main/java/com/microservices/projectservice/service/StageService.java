package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.StageCreateRequest;
import com.microservices.projectservice.dto.StageResponse;
import com.microservices.projectservice.dto.StageUpdateRequest;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.StageRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final FormRepository formRepository;

    public StageResponse getStage(@NotNull String stageId) throws NoEntityFoundException {
        var stage = findStage(stageId);
        return new StageResponse(
                stage.getId(),
                stage.getName(),
                stage.getDescription(),
                stage.getStartDate(),
                stage.getEndDate(),
                stage.getForm().getId(),
                stage.getProjectOwner().getId()
        );
    }

    public String createStage(@NotNull StageCreateRequest stageCreateRequest)
            throws NoEntityFoundException, IllegalAttributeException {
        var name = stageCreateRequest.name();
        if (name == null || name.isBlank() || name.isEmpty())
            throw new IllegalAttributeException("Stage name cannot be null/empty/blank");

        LocalDate startDate = stageCreateRequest.startDate(),
                endDate = stageCreateRequest.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Start date cannot be after end date");

        var formId = stageCreateRequest.formId();
        if (formId == null || formId.isBlank() || formId.isEmpty())
            throw new IllegalAttributeException("Form ID cannot be null/empty/blank");
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("Form ID is not available, input ID: " + formId));

        var projectOwnerId = stageCreateRequest.projectOwnerId();
        if (projectOwnerId == null || projectOwnerId.isBlank() || projectOwnerId.isEmpty())
            throw new IllegalAttributeException("Project owner ID cannot be null/empty/blank");
        var project = projectRepository.findById(projectOwnerId)
                .orElseThrow(() -> new NoEntityFoundException("Project owner ID is not available, input ID: " + projectOwnerId));

        var stage = Stage.builder()
                .name(name)
                .description(stageCreateRequest.description())
                .form(form)
                .projectOwner(project)
                .build();
        return stageRepository.save(stage).getId();
    }

    public void updateStage(@NotNull String stageId, @NotNull StageUpdateRequest stageUpdateRequest)
            throws NoEntityFoundException, IllegalAttributeException {
        var stage = findStage(stageId);
        String name = stageUpdateRequest.name(),
                description = stageUpdateRequest.description();
        if (name != null) {
            if (name.isBlank() || name.isEmpty())
                throw new IllegalAttributeException("Stage name cannot be null/empty/blank");
            stage.setName(name);
        }
        if (description != null) stage.setDescription(description);

        LocalDate startDate = stageUpdateRequest.startDate(),
                endDate = stageUpdateRequest.endDate();
        if (startDate != null) stage.setStartDate(startDate);
        if (endDate != null) stage.setEndDate(endDate);
        if ((startDate != null && startDate.isAfter(stage.getEndDate()))
            || (endDate != null && endDate.isBefore(stage.getStartDate())))
            throw new IllegalAttributeException("Start date of the stage cannot be greater than end date");

        var formId = stageUpdateRequest.formId();
        if (formId != null) {
            if (formId.isBlank() || formId.isEmpty())
                throw new IllegalAttributeException("Form ID cannot be null/empty/blank");
            var form = formRepository.findById(formId)
                    .orElseThrow(() -> new NoEntityFoundException("Form ID is not available, input ID: " + formId));
            stage.setForm(form);
        }

        stageRepository.save(stage);
    }

    public void deleteStage(@NotNull String stageId) throws NoEntityFoundException {
        var stage = findStage(stageId);
        stageRepository.delete(stage);
    }

    private Stage findStage(@NotNull String stageId) throws NoEntityFoundException {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoEntityFoundException("No stage found with id: " + stageId));
    }
}
