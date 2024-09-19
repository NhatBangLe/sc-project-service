package com.microservices.projectservice.service;

import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.dto.request.ProjectCreateRequest;
import com.microservices.projectservice.dto.request.ProjectMemberRequest;
import com.microservices.projectservice.dto.response.ProjectResponse;
import com.microservices.projectservice.dto.request.ProjectUpdateRequest;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.User;
import com.microservices.projectservice.exception.*;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse getProject(@NotNull String projectId) throws NoEntityFoundException {
        var project = findProject(projectId);
        return mapProjectToResponse(project);
    }

    public List<ProjectResponse> getAllOwnProjects(
            @NotBlank(message = "User ID cannot be null/blank when getting all own projects.") String userId,
            @NotNull(message = "Project status cannot be null when getting all own projects.") ProjectStatus status,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all own projects.")
            @NotNull(message = "Page number cannot be null when getting all own projects.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all own projects.")
            @NotNull(message = "Page size cannot be null when getting all own projects.")
            Integer pageSize
    ) throws NoEntityFoundException {
        var pageable = createPageable(pageNumber, pageSize);
        var user = findUser(userId);

        return projectRepository.findAllByOwner(user, pageable).stream()
                .filter(project -> project.getStatus().equals(status))
                .map(this::mapProjectToResponse)
                .toList();
    }

    public List<ProjectResponse> getAllJoinProjects(
            @NotBlank(message = "User ID cannot be null/blank when getting all join projects.") String userId,
            @NotNull(message = "Project status cannot be null when getting all join projects.") ProjectStatus status,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all join projects.")
            @NotNull(message = "Page number cannot be null when getting all join projects.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all join projects.")
            @NotNull(message = "Page size cannot be null when getting all join projects.")
            Integer pageSize
    ) throws NoEntityFoundException {
        var pageable = createPageable(pageNumber, pageSize);
        var user = findUser(userId);

        return projectRepository.findAllByMembersContains(user, pageable).stream()
                .filter(project -> project.getStatus().equals(status))
                .map(this::mapProjectToResponse)
                .toList();
    }

    public String createProject(
            @NotNull(message = "Creating project data cannot be null.")
            @Valid ProjectCreateRequest projectCreateRequest
    ) throws IllegalAttributeException {
        LocalDate startDate = projectCreateRequest.startDate(),
                endDate = projectCreateRequest.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Project start date cannot be greater than end date.");

        String userOwnerId = projectCreateRequest.ownerId();

        var projectBuilder = Project.builder().name(projectCreateRequest.name())
                .thumbnailId(projectCreateRequest.thumbnailId())
                .description(projectCreateRequest.description())
                .startDate(startDate)
                .endDate(endDate)
                .status(ProjectStatus.NORMAL);
        userRepository.findById(userOwnerId).ifPresentOrElse(
                projectBuilder::owner,
                () -> projectBuilder.owner(User.builder().id(userOwnerId).build())
        );
        var memberIds = projectCreateRequest.memberIds();
        if (memberIds != null)
            projectBuilder.members(memberIds.stream()
                    .map(memberId -> User.builder().id(memberId).build())
                    .collect(Collectors.toSet()));

        return projectRepository.save(projectBuilder.build()).getId();
    }

    public void updateProject(
            @NotBlank(message = "Project ID cannot be null/blank when updating a project.") String projectId,
            @NotNull(message = "Updating project data cannot be null.") ProjectUpdateRequest projectUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var project = findProject(projectId);

        String name = projectUpdateRequest.name(),
                description = projectUpdateRequest.description();
        if (name != null) {
            if (name.isBlank()) throw new IllegalAttributeException("Project name cannot be empty.");
            project.setName(name);
            isUpdated = true;
        }
        if (description != null) project.setDescription(description);

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
    ) throws NoEntityFoundException {
        var project = findProject(projectId);
        var memberId = projectMemberRequest.memberId();

        switch (projectMemberRequest.operator()) {
            case ADD -> project.getMembers().add(User.builder().id(memberId).build());
            case REMOVE -> project.getMembers().removeIf(user -> user.getId().equals(memberId));
        }
        projectRepository.save(project);
    }

    public void deleteProject(
            @NotBlank(message = "Project ID cannot be null/blank when deleting a project.") String projectId
    ) throws NoEntityFoundException {
        var project = findProject(projectId);
        projectRepository.delete(project);
    }

    private Project findProject(String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    private User findUser(String userId) throws NoEntityFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("No user found with id: " + userId));
    }

    private Pageable createPageable(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber, pageSize);
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
                project.getOwner().getId(),
                memberIds
        );
    }

}
