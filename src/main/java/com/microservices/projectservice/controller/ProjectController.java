package com.microservices.projectservice.controller;

import com.microservices.projectservice.constant.ProjectQueryType;
import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.dto.request.ProjectCreateRequest;
import com.microservices.projectservice.dto.request.ProjectMemberRequest;
import com.microservices.projectservice.dto.request.ProjectUpdateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.ProjectResponse;
import com.microservices.projectservice.mapper.ProjectMapper;
import com.microservices.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(path = "/api/${app.api-version}/project")
@RequiredArgsConstructor
@Tag(name = "Project", description = "All endpoints about projects.")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper mapper;

    @GetMapping(path = "/{userId}/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get all projects own/join by user having the userId.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page number or page size.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "User ID is not available.", content = @Content)
    })
    public PagingObjectsResponse<ProjectResponse> getAllProjects(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String userId,
            @RequestParam(required = false, defaultValue = "ALL")
            ProjectQueryType query,
            @RequestParam(required = false, defaultValue = "NORMAL")
            ProjectStatus status,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "Invalid page number (cannot be less than 0).")
            Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6")
            @Min(value = 1, message = "Invalid page size (must greater than 0).")
            Integer pageSize
    ) {
        var projects = projectService.getAllProjects(userId, query, status, pageNumber, pageSize);
        var pagingResponse = new PagingObjectsResponse<>(
                projects.getTotalPages(),
                projects.getTotalElements(),
                projects.getNumber(),
                projects.getSize(),
                projects.getNumberOfElements(),
                projects.isFirst(),
                projects.isLast(),
                projects.stream().toList()
        );
        return pagingResponse.map(mapper::toResponse);
    }

    @GetMapping(path = "/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get a project by its id.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid projectId.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found.",
                    content = @Content
            )
    })
    public ProjectResponse getProject(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId
    ) {
        var project = projectService.getProject(projectId);
        return mapper.toResponse(project);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create a project.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully. Response: The ID of the created project."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project name or Owner ID is null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Related service(s) are not available.",
                    content = @Content
            )
    })
    public String createProject(@RequestBody @Valid ProjectCreateRequest projectCreateRequest) {
        return projectService.createProject(projectCreateRequest);
    }

    @GetMapping(path = "/{projectId}/stage/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Check if userId joined any stages in the project.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid projectId/userId.",
                    content = @Content
            ),
    })
    public boolean checkUserInAnyStage(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId,
            @PathVariable
            @Size(min = 36, max = 36, message = "userId length must be 36 characters.")
            String userId
    ) {
        return projectService.checkUserInAnyStage(projectId, userId);
    }

    @PatchMapping(path = "/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Update a specific project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project updated successfully."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project name is blank or Start date is greater than end date."
            ),
            @ApiResponse(responseCode = "404", description = "Project not found.")
    })
    public void updateProject(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId,
            @RequestBody ProjectUpdateRequest projectUpdateRequest
    ) {
        projectService.updateProject(projectId, projectUpdateRequest);
    }

    @PatchMapping(path = "/{projectId}/member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Update a member for a specific project.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project member updated successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Member ID is null/blank or Invalid operator."),
            @ApiResponse(responseCode = "404", description = "Project not found."),
            @ApiResponse(
                    responseCode = "503",
                    description = "Related service(s) are not available.",
                    content = @Content
            )
    })
    public void updateProjectMember(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId,
            @RequestBody @Valid ProjectMemberRequest body
    ) {
        projectService.updateMember(projectId, body);
    }

    @DeleteMapping(path = "/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found."
            )
    })
    public void deleteProject(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId
    ) {
        projectService.deleteProject(projectId);
    }

}
