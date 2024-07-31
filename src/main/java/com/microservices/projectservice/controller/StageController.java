package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.StageCreateRequest;
import com.microservices.projectservice.dto.StageResponse;
import com.microservices.projectservice.dto.StageUpdateRequest;
import com.microservices.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/${api.version}/stage")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "All endpoints about stages.")
public class StageController {

    private final StageService stageService;

    @GetMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Stage ID is not available.", content = @Content)
    public StageResponse getStage(@PathVariable String stageId) {
        return stageService.getStage(stageId);
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
                    description = "Stage name, Form ID or Project owner ID is null/empty/blank. " +
                                  "Otherwise, stage start date is greater than end date.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project owner ID or Form ID is not available.",
                    content = @Content
            )
    })
    public String createStage(@RequestBody StageCreateRequest stageCreateRequest) {
        return stageService.createStage(stageCreateRequest);
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
    public void updateStage(@PathVariable String stageId, @RequestBody StageUpdateRequest stageUpdateRequest) {
        stageService.updateStage(stageId, stageUpdateRequest);
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
                    description = "Stage ID is not available."
            )
    })
    public void deleteStage(@PathVariable String stageId) {
        stageService.deleteStage(stageId);
    }

}
