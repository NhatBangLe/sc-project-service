package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.AnswerUpsertRequest;
import com.microservices.projectservice.dto.request.SampleCreateRequest;
import com.microservices.projectservice.dto.request.SampleUpdateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.SampleResponse;
import com.microservices.projectservice.service.SampleService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/${API.VERSION}/sample")
@RequiredArgsConstructor
@Tag(name = "Sample", description = "All endpoints about samples.")
public class SampleController {

    private final SampleService sampleService;

    @GetMapping(path = "/{projectId}/project")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Project ID is not available.", content = @Content)
    public PagingObjectsResponse<SampleResponse> getAllSamplesByProjectId(
            @PathVariable String projectId,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6") Integer pageSize
    ) {
        return sampleService.getAllSamplesByProjectId(projectId, pageNumber, pageSize);
    }

    @GetMapping(path = "/{stageId}/stage")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Stage ID is not available.", content = @Content)
    public PagingObjectsResponse<SampleResponse> getAllSamplesByStageId(
            @PathVariable String stageId,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6") Integer pageSize
    ) {
        return sampleService.getAllSamplesByStageId(stageId, pageNumber, pageSize);
    }

    @GetMapping(path = "/{sampleId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Sample ID is not available.", content = @Content)
    public SampleResponse getSample(@PathVariable String sampleId) {
        return sampleService.getSample(sampleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sample created successfully. Response: The ID of the created sample."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project owner ID, Stage ID or some Field IDs may be null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project owner ID, Stage ID or some Field IDs may be not available.",
                    content = @Content
            )
    })
    public String createSample(@RequestBody SampleCreateRequest sampleCreateRequest) {
        return sampleService.createSample(sampleCreateRequest);
    }

    @PatchMapping(path = "/{sampleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sample updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sample ID is null/empty/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sample ID is not available.",
                    content = @Content
            )
    })
    public void updateSample(@PathVariable String sampleId, @RequestBody SampleUpdateRequest sampleUpdateRequest) {
        sampleService.updateSample(sampleId, sampleUpdateRequest);
    }

    @PatchMapping(path = "/{sampleId}/answer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Answer updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sample ID or Field ID is null/empty/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Answer is not available with passing Sample ID and Field ID.",
                    content = @Content
            )
    })
    public void updateAnswer(@PathVariable String sampleId, @RequestBody AnswerUpsertRequest answerUpsertRequest) {
        sampleService.updateAnswer(sampleId, answerUpsertRequest);
    }

    @DeleteMapping(path = "/{sampleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sample deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sample ID is not available.",
                    content = @Content
            )
    })
    public void deleteSample(@PathVariable String sampleId) {
        sampleService.deleteSample(sampleId);
    }

}
