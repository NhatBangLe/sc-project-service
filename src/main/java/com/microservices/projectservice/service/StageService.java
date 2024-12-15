package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.StageCreateRequest;
import com.microservices.projectservice.dto.request.StageMemberRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.StageResponse;
import com.microservices.projectservice.dto.request.StageUpdateRequest;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.entity.User;
import com.microservices.projectservice.exception.DataConflictException;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.StageRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final FormRepository formRepository;

    private final FileService fileService;

    public PagingObjectsResponse<StageResponse> getAllStages(
            @NotBlank(message = "Project ID cannot be null/blank when getting all stages.") String projectId,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all stages.")
            @NotNull(message = "Page number cannot be null when getting all stages.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all stages.")
            @NotNull(message = "Page size cannot be null when getting all stages.")
            Integer pageSize
    ) {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        var stages = stageRepository.findAllByProjectOwner_Id(projectId, pageable);
        return new PagingObjectsResponse<>(
                stages.getTotalPages(),
                stages.getTotalElements(),
                stages.getNumber(),
                stages.getSize(),
                stages.getNumberOfElements(),
                stages.isFirst(),
                stages.isLast(),
                stages.map(this::mapStageToResponse).toList()
        );
    }

    public StageResponse getStage(
            @NotBlank(message = "Stage ID cannot be null/blank when getting a stage.") String stageId
    ) throws NoEntityFoundException {
        var stage = findStage(stageId);
        return mapStageToResponse(stage);
    }

    public String createStage(
            @NotNull(message = "The creating stage data cannot be null.")
            @Valid
            StageCreateRequest stageCreateRequest
    ) throws NoEntityFoundException, IllegalAttributeException {
        LocalDate startDate = stageCreateRequest.startDate(),
                endDate = stageCreateRequest.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Stage start date cannot be after end date.");

        var project = findProject(stageCreateRequest.projectOwnerId());
        var stageBuilder = Stage.builder()
                .name(stageCreateRequest.name())
                .description(stageCreateRequest.description())
                .startDate(startDate)
                .endDate(endDate)
                .projectOwner(project);

        var formId = stageCreateRequest.formId();
        if (formId != null && !formId.isBlank()) {
            var form = findForm(formId);
            stageBuilder.form(form);
        }

        var memberIds = stageCreateRequest.memberIds();
        var projectMembers = project.getMembers();
        if (memberIds != null && projectMembers != null) {
            var memberNotInProjectError = "There have user IDs not in the same project.";
            if (projectMembers.size() < memberIds.size())
                throw new IllegalAttributeException(memberNotInProjectError);

            var newMembers = projectMembers.stream()
                    .filter(user -> memberIds.contains(user.getId()))
                    .collect(Collectors.toSet());
            if (newMembers.size() < memberIds.size())
                throw new IllegalAttributeException(memberNotInProjectError);
            stageBuilder.members(newMembers);
        }

        return stageRepository.save(stageBuilder.build()).getId();
    }

    public void updateStage(
            @NotBlank(message = "Stage ID cannot be null/blank when updating a stage.") String stageId,
            @NotNull(message = "The updating stage data cannot be null.")
            StageUpdateRequest stageUpdateRequest
    ) throws NoEntityFoundException, IllegalAttributeException {
        var isUpdated = false;
        var stage = findStage(stageId);
        String name = stageUpdateRequest.name(),
                description = stageUpdateRequest.description();
        if (name != null) {
            if (name.isBlank()) throw new IllegalAttributeException("Stage name cannot be null/blank.");
            stage.setName(name);
            isUpdated = true;
        }
        if (description != null) {
            stage.setDescription(description);
            isUpdated = true;
        }

        var startDate = stageUpdateRequest.startDate();
        var endDate = stageUpdateRequest.endDate();
        if (startDate != null) {
            stage.setStartDate(startDate);
            isUpdated = true;
        }
        if (endDate != null) {
            stage.setEndDate(endDate);
            isUpdated = true;
        }
        if ((startDate != null && startDate.isAfter(stage.getEndDate()))
            || (endDate != null && endDate.isBefore(stage.getStartDate())))
            throw new IllegalAttributeException("Stage start date cannot be after end date.");

        var formId = stageUpdateRequest.formId();
        if (formId != null) {
            if (formId.isBlank()) throw new IllegalAttributeException("Form ID cannot be null/blank");
            var form = findForm(formId);
            stage.setForm(form);
            isUpdated = true;
        }

        if (isUpdated) stageRepository.save(stage);
    }

    public void updateMember(
            @NotBlank(message = "Stage ID cannot be null/blank when updating member to stage.") String stageId,
            @NotNull(message = "The updating data cannot be null.")
            StageMemberRequest stageMemberRequest
    ) throws NoEntityFoundException, IllegalAttributeException {
        var memberId = stageMemberRequest.memberId();

        var isUpdated = false;
        var stage = findStage(stageId);

        switch (stageMemberRequest.operator()) {
            case ADD -> {
                var existMembers = stage.getMembers();
                var isMemberIdExisted = existMembers.stream()
                        .anyMatch(member -> member.getId().equals(memberId));
                if (isMemberIdExisted)
                    throw new DataConflictException("Member already exists.");

                stage.getProjectOwner().getMembers().forEach(member -> {
                    if (member.getId().equals(memberId))
                        existMembers.add(member);
                });
                if (existMembers.isEmpty())
                    throw new DataConflictException("Member does not exist in the project.");
                isUpdated = true;
            }
            case REMOVE -> {
                var isRemoved = stage.getMembers()
                        .removeIf(user -> user.getId().equals(memberId));
                if (isRemoved) isUpdated = true;
                else throw new DataConflictException("Member does not exist.");
            }
        }
        if (isUpdated) stageRepository.save(stage);
    }

    public void deleteStage(
            @NotBlank(message = "Stage ID cannot be null/blank when deleting a stage.") String stageId
    ) throws NoEntityFoundException {
        var stage = findStage(stageId);

        // Delete all sample images
        stage.getSamples().forEach(sample -> fileService.deleteFile(sample.getAttachmentId()));

        stageRepository.delete(stage);
    }

    private Project findProject(String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    private Stage findStage(String stageId) throws NoEntityFoundException {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoEntityFoundException("No stage found with id: " + stageId));
    }

    private Form findForm(String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    private StageResponse mapStageToResponse(Stage stage) {
        var memberIds = stage.getMembers().parallelStream().map(User::getId).toList();
        return new StageResponse(
                stage.getId(),
                stage.getName(),
                stage.getDescription(),
                stage.getStartDate(),
                stage.getEndDate(),
                stage.getForm().getId(),
                stage.getCreatedAt().getTime(),
                stage.getProjectOwner().getId(),
                memberIds
        );
    }

}
