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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse getProject(@NotNull String projectId) throws NoEntityFoundException {
        var project = findProject(projectId);
        return mapProjectToResponse(project);
    }

    public List<ProjectResponse> getAllOwnProjects(@NotNull String userId,
                                                   @NotNull ProjectStatus status,
                                                   @NotNull Integer pageNumber,
                                                   @NotNull Integer pageSize)
            throws IllegalAttributeException, NoEntityFoundException {
        var pageable = createPageable(pageNumber, pageSize);
        var user = findUser(userId);

        return projectRepository.findAllByOwner(user, pageable).stream()
                .filter(project -> project.getStatus().equals(status))
                .map(this::mapProjectToResponse)
                .toList();
    }

    public List<ProjectResponse> getAllJoinProjects(@NotNull String userId,
                                                    @NotNull ProjectStatus status,
                                                    @NotNull Integer pageNumber,
                                                    @NotNull Integer pageSize)
            throws IllegalAttributeException, NoEntityFoundException {
        var pageable = createPageable(pageNumber, pageSize);
        var user = findUser(userId);

        return projectRepository.findAllByMembersContains(user, pageable).stream()
                .filter(project -> project.getStatus().equals(status))
                .map(this::mapProjectToResponse)
                .toList();
    }

    public String createProject(@NotNull ProjectCreateRequest projectCreateRequest) throws IllegalAttributeException {
        String projectName = projectCreateRequest.name(),
                ownerId = projectCreateRequest.ownerId();
        if (projectName == null || projectName.isEmpty() || projectName.isBlank())
            throw new IllegalAttributeException("Project name cannot be empty");
        if (ownerId == null || ownerId.isEmpty() || ownerId.isBlank())
            throw new IllegalAttributeException("Owner ID cannot be empty");

        LocalDate startDate = projectCreateRequest.startDate(),
                endDate = projectCreateRequest.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Project start date cannot be greater than end date");

        var projectBuilder = Project.builder().name(projectName)
                .description(projectCreateRequest.description())
                .startDate(startDate)
                .endDate(endDate)
                .status(ProjectStatus.NORMAL);
        userRepository.findById(ownerId)
                .ifPresentOrElse(projectBuilder::owner,
                        () -> projectBuilder.owner(User.builder().id(ownerId).build()));
        var memberIds = projectCreateRequest.memberIds();
        if (memberIds != null)
            projectBuilder.members(memberIds.stream()
                    .map(memberId -> User.builder().id(memberId).build())
                    .collect(Collectors.toSet()));

        return projectRepository.save(projectBuilder.build()).getId();
    }

    public void updateProject(@NotNull String projectId, @NotNull ProjectUpdateRequest projectUpdateRequest)
            throws NoEntityFoundException, IllegalAttributeException {
        var project = findProject(projectId);

        String name = projectUpdateRequest.name(),
                description = projectUpdateRequest.description();
        if (name != null) {
            if (name.isBlank() || name.isEmpty()) throw new IllegalAttributeException("Project name cannot be empty");
            project.setName(name);
        }
        if (description != null) project.setDescription(description);

        var status = projectUpdateRequest.status();
        if (status != null) {
            if (!Arrays.asList(ProjectStatus.values()).contains(status))
                throw new IllegalAttributeException("Invalid project status");
            project.setStatus(status);
        }

        LocalDate startDate = projectUpdateRequest.startDate(),
                endDate = projectUpdateRequest.endDate();
        if (startDate != null) project.setStartDate(startDate);
        if (endDate != null) project.setEndDate(endDate);
        if ((startDate != null && startDate.isAfter(project.getEndDate()))
            || (endDate != null && endDate.isBefore(project.getStartDate())))
            throw new IllegalAttributeException("Project start date cannot be greater than end date");

        projectRepository.save(project);
    }

    public void updateMember(@NotNull String projectId, @NotNull ProjectMemberRequest projectMemberRequest)
            throws NoEntityFoundException, IllegalAttributeException, UnexpectedOperatorException {
        var operator = projectMemberRequest.operator();
        var project = findProject(projectId);
        var memberId = projectMemberRequest.memberId();
        if (memberId == null || memberId.isEmpty() || memberId.isBlank())
            throw new IllegalAttributeException("Member ID cannot be null/empty/blank");

        switch (operator) {
            case ADD -> project.getMembers().add(User.builder().id(memberId).build());
            case REMOVE -> project.getMembers().removeIf(user -> user.getId().equals(memberId));
            default -> throw new UnexpectedOperatorException("Project member operator is not supported");
        }

        projectRepository.save(project);
    }

    public void deleteProject(@NotNull String projectId) throws NoEntityFoundException {
        var project = findProject(projectId);
        projectRepository.delete(project);
    }

    private Project findProject(@NotNull String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    private User findUser(@NotNull String userId) throws NoEntityFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("No user found with id: " + userId));
    }

    private Pageable createPageable(@NotNull Integer pageNumber, @NotNull Integer pageSize)
            throws IllegalAttributeException {
        if (pageNumber < 0 || pageSize <= 0)
            throw new IllegalAttributeException("Invalid page number or page size");
        return PageRequest.of(pageNumber, pageSize);
    }

    private ProjectResponse mapProjectToResponse(@NotNull Project project) {
        var memberIds = project.getMembers().parallelStream().map(User::getId).toList();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getOwner().getId(),
                memberIds
        );
    }

}
