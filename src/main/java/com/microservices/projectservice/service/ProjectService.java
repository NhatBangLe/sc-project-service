package com.microservices.projectservice.service;

import com.microservices.projectservice.constant.ProjectQueryType;
import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.dto.request.ProjectCreateRequest;
import com.microservices.projectservice.dto.request.ProjectMemberRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.ProjectResponse;
import com.microservices.projectservice.dto.request.ProjectUpdateRequest;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.User;
import com.microservices.projectservice.exception.*;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.StageRepository;
import com.microservices.projectservice.repository.UserRepository;
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
import java.util.HashSet;

@Service
@Validated
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;

    private final UserService userService;
    private final FileService fileService;

    public ProjectResponse getProject(@NotNull String projectId) throws NoEntityFoundException {
        var project = findProject(projectId);
        return mapProjectToResponse(project);
    }

    public PagingObjectsResponse<ProjectResponse> getAllProjects(
            @NotBlank(message = "User ID cannot be null/blank when getting all own projects.") String userId,
            @NotNull(message = "Query type cannot be null when getting all own projects.") ProjectQueryType query,
            @NotNull(message = "Project status cannot be null when getting all own projects.") ProjectStatus status,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all own projects.")
            @NotNull(message = "Page number cannot be null when getting all own projects.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all own projects.")
            @NotNull(message = "Page size cannot be null when getting all own projects.")
            Integer pageSize
    ) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        var projects = switch (query) {
            case ALL -> projectRepository.findAllProjectByUserIdAndStatus(userId, status, pageable);
            case OWN -> projectRepository.findAllByOwner_IdAndStatus(userId, status, pageable);
            case JOIN -> projectRepository.findAllByMembers_IdAndStatus(userId, status, pageable);
        };

        return new PagingObjectsResponse<>(
                projects.getTotalPages(),
                projects.getTotalElements(),
                projects.getNumber(),
                projects.getSize(),
                projects.getNumberOfElements(),
                projects.isFirst(),
                projects.isLast(),
                projects.map(this::mapProjectToResponse).toList()
        );
    }

    public String createProject(
            @NotNull(message = "Creating project data cannot be null.")
            @Valid ProjectCreateRequest projectCreateRequest
    ) throws IllegalAttributeException, DataConflictException {
        LocalDate startDate = projectCreateRequest.startDate(),
                endDate = projectCreateRequest.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Project start date cannot be greater than end date.");

        var userOwnerId = projectCreateRequest.ownerId();
        var owner = userRepository.findById(userOwnerId)
                .orElse(User.builder().id(userOwnerId).build());
        var projectBuilder = Project.builder()
                .name(projectCreateRequest.name())
                .description(projectCreateRequest.description())
                .startDate(startDate)
                .endDate(endDate)
                .status(ProjectStatus.NORMAL)
                .owner(owner);

        var thumbnailId = projectCreateRequest.thumbnailId();
        if (thumbnailId != null && !thumbnailId.isBlank())
            projectBuilder.thumbnailId(thumbnailId);

        var memberIds = projectCreateRequest.memberIds();
        if (memberIds != null) {
            // take id not equal to ownerId only
            var filteredMemberIds = memberIds.stream().filter(id -> !id.equals(userOwnerId)).toList();
            var existedUsers = userRepository.findAllById(filteredMemberIds);

            // check users not existed
            if (existedUsers.size() != filteredMemberIds.size()) {
                var existedIds = existedUsers.stream().map(User::getId).toList();
                var newUsers = filteredMemberIds.stream()
                        .filter(id -> !existedIds.contains(id))
                        .map(id -> {
                            // Check if user not exists
                            var response = userService.getUser(id);
                            if (response.getStatusCode().isError())
                                throw new DataConflictException("Member with id " + id + " not found.");

                            return User.builder().id(id).build();
                        })
                        .toList();
                existedUsers.addAll(newUsers);
            }
            projectBuilder.members(new HashSet<>(existedUsers));
        }

        return projectRepository.save(projectBuilder.build()).getId();
    }

    public boolean checkUserInAnyStage(String projectId, String userId) {
        return stageRepository.existsByProjectOwner_IdAndMembers_Id(projectId, userId);
    }

    public void updateProject(
            @NotBlank(message = "Project ID cannot be null/blank when updating a project.") String projectId,
            @NotNull(message = "Updating project data cannot be null.") ProjectUpdateRequest projectUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException, DataConflictException {
        var isUpdated = false;
        var project = findProject(projectId);

        var currentThumbnailId = project.getThumbnailId();
        var newThumbnailId = projectUpdateRequest.thumbnailId();
        if (newThumbnailId != null &&
            !newThumbnailId.isBlank() &&
            !newThumbnailId.equals(currentThumbnailId)
        ) {
            if (currentThumbnailId != null) fileService.deleteFile(currentThumbnailId);

            project.setThumbnailId(newThumbnailId);
            isUpdated = true;
        }

        String name = projectUpdateRequest.name(),
                description = projectUpdateRequest.description();
        if (name != null) {
            if (name.isBlank()) throw new IllegalAttributeException("Project name cannot be empty.");
            project.setName(name);
            isUpdated = true;
        }

        if (description != null)
            project.setDescription(description);

        var status = projectUpdateRequest.status();
        if (status != null) {
            project.setStatus(status);
            isUpdated = true;
        }

        LocalDate startDate = projectUpdateRequest.startDate(),
                endDate = projectUpdateRequest.endDate();
        if (startDate != null) {
            project.setStartDate(startDate);
            isUpdated = true;
        }
        if (endDate != null) {
            project.setEndDate(endDate);
            isUpdated = true;
        }
        if ((startDate != null && startDate.isAfter(project.getEndDate()))
            || (endDate != null && endDate.isBefore(project.getStartDate())))
            throw new IllegalAttributeException("Project start date cannot be greater than end date.");

        if (isUpdated) projectRepository.save(project);
    }

    public void updateMember(
            @NotBlank(message = "Project ID cannot be null/blank when updating project member.") String projectId,
            @NotNull(message = "Updating project member data cannot be null.")
            @Valid
            ProjectMemberRequest projectMemberRequest
    ) throws NoEntityFoundException, DataConflictException, IllegalAttributeException {
        var memberId = projectMemberRequest.memberId();
        var response = userService.getUser(memberId);
        if (response.getStatusCode().isError())
            throw new IllegalAttributeException("Member with id " + memberId + " not found.");

        var isUpdated = false;
        var project = findProject(projectId);

        switch (projectMemberRequest.operator()) {
            case ADD -> {
                var existMembers = project.getMembers();
                var isMemberIdExisted = existMembers.stream()
                        .anyMatch(member -> member.getId().equals(memberId));
                if (isMemberIdExisted)
                    throw new DataConflictException("Member already exists.");

                var user = userRepository.findById(memberId)
                        .orElse(User.builder().id(memberId).build());
                existMembers.add(user);
                isUpdated = true;
            }
            case REMOVE -> {
                var isRemoved = project.getMembers()
                        .removeIf(user -> user.getId().equals(memberId));
                if (isRemoved) isUpdated = true;
                else throw new DataConflictException("Member does not exist.");
            }
        }
        if (isUpdated) projectRepository.save(project);
    }

    public void deleteProject(
            @NotBlank(message = "Project ID cannot be null/blank when deleting a project.") String projectId
    ) throws NoEntityFoundException {
        var project = findProject(projectId);

        var thumbnailId = project.getThumbnailId();
        if (thumbnailId != null && !thumbnailId.isBlank())
            fileService.deleteFile(thumbnailId);

        // Removes sample images
        project.getSamples().forEach(sample -> fileService.deleteFile(sample.getAttachmentId()));

        projectRepository.delete(project);
    }

    private Project findProject(String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    private ProjectResponse mapProjectToResponse(Project project) {
        var memberIds = project.getMembers().parallelStream().map(User::getId).toList();
        return new ProjectResponse(
                project.getId(),
                project.getThumbnailId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt().getTime(),
                project.getOwner().getId(),
                memberIds
        );
    }

}
