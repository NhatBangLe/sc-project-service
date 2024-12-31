package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.StageCreateRequest;
import com.microservices.projectservice.dto.request.StageMemberRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.StageResponse;
import com.microservices.projectservice.dto.request.StageUpdateRequest;
import com.microservices.projectservice.mapper.StageMapper;
import com.microservices.projectservice.service.StageService;
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
@RequestMapping(path = "/api/${app.api-version}/stage")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "All endpoints about stages.")
public class StageController {

    private final StageService stageService;
    private final StageMapper mapper;

    @GetMapping(path = "/{projectId}/project")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get all stages own by project having the projectId.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page number or page size.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Project not found.", content = @Content)
    })
    public PagingObjectsResponse<StageResponse> getAllStages(
            @PathVariable
            @Size(min = 36, max = 36, message = "projectId length must be 36 characters.")
            String projectId,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "Invalid page number (cannot be less than 0).")
            Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6")
            @Min(value = 1, message = "Invalid page size (must greater than 0).")
            Integer pageSize
    ) {
        var stages = stageService.getAllStages(projectId, pageNumber, pageSize);
        return new PagingObjectsResponse<>(
                stages.getTotalPages(),
                stages.getTotalElements(),
                stages.getNumber(),
                stages.getSize(),
                stages.getNumberOfElements(),
                stages.isFirst(),
                stages.isLast(),
                stages.map(mapper::toResponse).toList()
        );
    }

    @GetMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Stage not found.", content = @Content)
    public StageResponse getStage(
            @PathVariable
            @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
            String stageId
    ) {
        var stage = stageService.getStage(stageId);
        return mapper.toResponse(stage);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Stage created successfully. Response: The ID of the created stage."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Stage name, Form ID or Project owner ID is null/blank. " +
                                  "Otherwise, stage start date is greater than end date.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project owner or Form not found.",
                    content = @Content
            )
    })
    public String createStage(@RequestBody @Valid StageCreateRequest body) {
        return stageService.createStage(body);
    }

    @PatchMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Form updated successfully."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Stage name or Form ID is empty/blank. " +
                                  "Otherwise, stage start date is greater than end date."
            ),
            @ApiResponse(responseCode = "404", description = "Form ID is not available.")
    })
    public void updateStage(
            @PathVariable
            @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
            String stageId,
            @RequestBody StageUpdateRequest body
    ) {
        stageService.updateStage(stageId, body);
    }

    @PatchMapping(path = "/{stageId}/member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Stage member updated successfully."
            ),
            @ApiResponse(responseCode = "400",
                    description = "Member ID is null/blank, Invalid operator or an member is not in project."),
            @ApiResponse(responseCode = "404", description = "Stage ID is not available.")
    })
    public void updateProjectMember(
            @PathVariable
            @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
            String stageId,
            @RequestBody @Valid StageMemberRequest stageMemberRequest
    ) {
        stageService.updateMember(stageId, stageMemberRequest);
    }

    @DeleteMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Stage deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stage not found."
            )
    })
    public void deleteStage(
            @PathVariable
            @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
            String stageId
    ) {
        stageService.deleteStage(stageId);
    }

}
