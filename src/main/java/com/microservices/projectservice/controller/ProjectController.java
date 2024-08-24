package com.microservices.projectservice.controller;

import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.dto.request.ProjectCreateRequest;
import com.microservices.projectservice.dto.request.ProjectMemberRequest;
import com.microservices.projectservice.dto.request.ProjectUpdateRequest;
import com.microservices.projectservice.dto.response.ProjectResponse;
import com.microservices.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/${API.VERSION}/project")
@RequiredArgsConstructor
@Tag(name = "Project", description = "All endpoints about projects.")
public class ProjectController {

    private final ProjectService projectService;

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
    public List<ProjectResponse> getAllProjects(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "true") boolean isOwner,
            @RequestParam(required = false, defaultValue = "NORMAL") ProjectStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6") Integer pageSize) {
        return isOwner ? projectService.getAllOwnProjects(userId, status, pageNumber, pageSize)
                : projectService.getAllJoinProjects(userId, status, pageNumber, pageSize);
    }

    @GetMapping(path = "/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Project ID is not available.", content = @Content)
    public ProjectResponse getProject(@PathVariable String projectId) {
        return projectService.getProject(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully. Response: The ID of the created project."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project name or Owner ID is null/blank.",
                    content = @Content
            )
    })
    public String createProject(@RequestBody ProjectCreateRequest projectCreateRequest) {
        return projectService.createProject(projectCreateRequest);
    }

    @PatchMapping(path = "/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project updated successfully."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project name is blank or Start date is greater than end date."
            ),
            @ApiResponse(responseCode = "404", description = "Project ID is not available.")
    })
    public void updateProject(@PathVariable String projectId, @RequestBody ProjectUpdateRequest projectUpdateRequest) {
        projectService.updateProject(projectId, projectUpdateRequest);
    }

    @PatchMapping(path = "/{projectId}/member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project member updated successfully."
            ),
            @ApiResponse(responseCode = "400", description = "Member ID is null/blank or Invalid operator."),
            @ApiResponse(responseCode = "404", description = "Project ID is not available.")
    })
    public void updateProjectMember(@PathVariable String projectId, @RequestBody ProjectMemberRequest projectMemberRequest) {
        projectService.updateMember(projectId, projectMemberRequest);
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
                    description = "Project ID is not available."
            )
    })
    public void deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
    }

}
