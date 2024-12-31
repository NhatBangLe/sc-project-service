package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.StageCreateRequest;
import com.microservices.projectservice.dto.request.StageMemberRequest;
import com.microservices.projectservice.dto.request.StageUpdateRequest;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.exception.DataConflictException;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;

    private final ProjectService projectService;
    private final FormService formService;
    private final FileService fileService;

    public Page<Stage> getAllStages(String projectId,
                                    Integer pageNumber,
                                    Integer pageSize) throws NoEntityFoundException {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return stageRepository.findAllByProjectOwner_Id(projectId, pageable);
    }

    public Stage getStage(String stageId) throws NoEntityFoundException {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoEntityFoundException("No stage found with id: " + stageId));
    }

    public String createStage(StageCreateRequest body)
            throws NoEntityFoundException, IllegalAttributeException {
        LocalDate startDate = body.startDate(),
                endDate = body.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Stage start date cannot be after end date.");

        var project = projectService.getProject(body.projectOwnerId());
        var stageBuilder = Stage.builder()
                .name(body.name())
                .description(body.description())
                .startDate(startDate)
                .endDate(endDate)
                .projectOwner(project);

        var formId = body.formId();
        if (formId != null && !formId.isBlank()) {
            var form = formService.getForm(formId);
            stageBuilder.form(form);
        }

        var memberIds = body.memberIds();
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

    public void updateStage(String stageId, StageUpdateRequest body)
            throws NoEntityFoundException, IllegalAttributeException {
        var isUpdated = false;
        var stage = getStage(stageId);
        String name = body.name(),
                description = body.description();
        if (name != null) {
            if (name.isBlank()) throw new IllegalAttributeException("name cannot be null/blank.");
            if (name.length() > 100)
                throw new IllegalAttributeException("name cannot be longer than 100 characters.");
            stage.setName(name);
            isUpdated = true;
        }
        if (description != null) {
            if (description.length() > 255)
                throw new IllegalAttributeException("description cannot be longer than 255 characters.");
            stage.setDescription(description);
            isUpdated = true;
        }

        var startDate = body.startDate();
        var endDate = body.endDate();
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
            throw new IllegalAttributeException("startDate cannot be after endDate.");

        var formId = body.formId();
        if (formId != null) {
            if (formId.isBlank()) throw new IllegalAttributeException("Form ID cannot be blank.");
            if (formId.length() != 36) throw new IllegalAttributeException("Form ID length must be 36 characters.");

            var form = formService.getForm(formId);
            stage.setForm(form);
            isUpdated = true;
        }

        if (isUpdated) stageRepository.save(stage);
    }

    public void updateMember(String stageId, StageMemberRequest body)
            throws NoEntityFoundException, IllegalAttributeException {
        var memberId = body.memberId();

        var isUpdated = false;
        var stage = getStage(stageId);

        switch (body.operator()) {
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

    public void deleteStage(String stageId) throws NoEntityFoundException {
        var stage = getStage(stageId);

        // Delete all sample images
        stage.getSamples().forEach(sample -> fileService.deleteFile(sample.getAttachmentId()));

        stageRepository.delete(stage);
    }

}
