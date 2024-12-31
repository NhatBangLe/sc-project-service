package com.microservices.projectservice.service;

import com.microservices.projectservice.constant.ProjectQueryType;
import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.dto.request.ProjectCreateRequest;
import com.microservices.projectservice.dto.request.ProjectMemberRequest;
import com.microservices.projectservice.dto.request.ProjectUpdateRequest;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.User;
import com.microservices.projectservice.exception.*;
import com.microservices.projectservice.repository.ProjectRepository;
import com.microservices.projectservice.repository.StageRepository;
import com.microservices.projectservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;

    private final UserService userService;
    private final FileService fileService;

    public Project getProject(String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    public Page<Project> getAllProjects(String userId, ProjectQueryType query,
                                        ProjectStatus status, Integer pageNumber, Integer pageSize) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return switch (query) {
            case ALL -> projectRepository.findAllProjectByUserIdAndStatus(userId, status, pageable);
            case OWN -> projectRepository.findAllByOwner_IdAndStatus(userId, status, pageable);
            case JOIN -> projectRepository.findAllByMembers_IdAndStatus(userId, status, pageable);
        };
    }

    public String createProject(ProjectCreateRequest body) throws IllegalAttributeException {
        LocalDate startDate = body.startDate(),
                endDate = body.endDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new IllegalAttributeException("Project start date cannot be greater than end date.");

        var userOwnerId = body.ownerId();
        var isOwnerExists = Objects.requireNonNull(
                userService.checkUserExists(userOwnerId),
                "Cannot check user owner existence for user id: " + userOwnerId
        );
        if (!isOwnerExists)
            throw new IllegalAttributeException("User with id " + userOwnerId + " not found.");
        var owner = userRepository.findById(userOwnerId)
                .orElse(User.builder().id(userOwnerId).build());
        var projectBuilder = Project.builder()
                .name(body.name())
                .description(body.description())
                .startDate(startDate)
                .endDate(endDate)
                .status(ProjectStatus.NORMAL)
                .owner(owner);

        var thumbnailId = body.thumbnailId();
        if (thumbnailId != null && !thumbnailId.isBlank()) {
            var isThumbnailExists = Objects.requireNonNull(
                    fileService.checkFileExists(thumbnailId),
                    "Cannot check thumbnail existence."
            );
            if (!isThumbnailExists) throw new IllegalAttributeException("Thumbnail file does not exist.");

            projectBuilder.thumbnailId(thumbnailId);
        }

        var memberIds = body.memberIds();
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
                            var isExist = Objects.requireNonNull(
                                    userService.checkUserExists(id),
                                    "Cannot check existence for user id: " + id
                            );
                            if (!isExist)
                                throw new IllegalAttributeException("User with id " + id + " not found.");

                            return User.builder().id(id).build();
                        }).toList();
                existedUsers.addAll(newUsers);
            }
            projectBuilder.members(new HashSet<>(existedUsers));
        }

        return projectRepository.save(projectBuilder.build()).getId();
    }

    public boolean checkUserInAnyStage(String projectId, String userId) {
        return stageRepository.existsByProjectOwner_IdAndMembers_Id(projectId, userId);
    }

    public void updateProject(String projectId, ProjectUpdateRequest body)
            throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var project = getProject(projectId);

        var currentThumbnailId = project.getThumbnailId();
        var newThumbnailId = body.thumbnailId();
        if (newThumbnailId != null &&
            !newThumbnailId.isBlank() &&
            !newThumbnailId.equals(currentThumbnailId)
        ) {
            if (currentThumbnailId != null) fileService.deleteFile(currentThumbnailId);

            project.setThumbnailId(newThumbnailId);
            isUpdated = true;
        }

        String name = body.name(),
                description = body.description();
        if (name != null) {
            if (name.isBlank()) throw new IllegalAttributeException("Project name cannot be empty.");
            project.setName(name);
            isUpdated = true;
        }

        if (description != null)
            project.setDescription(description);

        var status = body.status();
        if (status != null) {
            project.setStatus(status);
            isUpdated = true;
        }

        LocalDate startDate = body.startDate(),
                endDate = body.endDate();
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

    public void updateMember(String projectId, ProjectMemberRequest body)
            throws NoEntityFoundException, IllegalAttributeException {
        var memberId = body.memberId();
        var isExist = Objects.requireNonNull(
                userService.checkUserExists(memberId),
                "Cannot check existence for user id: " + memberId
        );
        if (!isExist) throw new IllegalAttributeException("Member with id " + memberId + " not found.");

        var isUpdated = false;
        var project = getProject(projectId);

        switch (body.operator()) {
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

    public void deleteProject(String projectId) throws NoEntityFoundException {
        var project = getProject(projectId);

        var thumbnailId = project.getThumbnailId();
        if (thumbnailId != null && !thumbnailId.isBlank())
            fileService.deleteFile(thumbnailId);

        // Removes sample images
        project.getSamples().forEach(sample -> fileService.deleteFile(sample.getAttachmentId()));

        projectRepository.delete(project);
    }

}
