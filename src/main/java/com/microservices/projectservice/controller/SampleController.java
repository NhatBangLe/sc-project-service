package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.AnswerUpsertRequest;
import com.microservices.projectservice.dto.request.SampleCreateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.SampleResponse;
import com.microservices.projectservice.mapper.SampleMapper;
import com.microservices.projectservice.service.SampleService;
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
@RequestMapping(path = "/api/${app.api-version}/sample")
@RequiredArgsConstructor
@Tag(name = "Sample", description = "All endpoints about samples.")
public class SampleController {

    private final SampleService sampleService;
    private final SampleMapper mapper;

    @GetMapping(path = "/{projectId}/project")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Project not found.", content = @Content)
    public PagingObjectsResponse<SampleResponse> getAllSamplesByProjectId(
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
        var samples = sampleService.getAllSamplesByProjectId(projectId, pageNumber, pageSize);
        return new PagingObjectsResponse<>(
                samples.getTotalPages(),
                samples.getTotalElements(),
                samples.getNumber(),
                samples.getSize(),
                samples.getNumberOfElements(),
                samples.isFirst(),
                samples.isLast(),
                samples.map(mapper::toResponse).toList()
        );
    }

    @GetMapping(path = "/{stageId}/stage")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Stage not found.", content = @Content)
    public PagingObjectsResponse<SampleResponse> getAllSamplesByStageId(
            @PathVariable
            @Size(min = 36, max = 36, message = "stageId length must be 36 characters.")
            String stageId,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "Invalid page number (cannot be less than 0).")
            Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6")
            @Min(value = 1, message = "Invalid page size (must greater than 0).")
            Integer pageSize
    ) {
        var samples = sampleService.getAllSamplesByStageId(stageId, pageNumber, pageSize);
        return new PagingObjectsResponse<>(
                samples.getTotalPages(),
                samples.getTotalElements(),
                samples.getNumber(),
                samples.getSize(),
                samples.getNumberOfElements(),
                samples.isFirst(),
                samples.isLast(),
                samples.map(mapper::toResponse).toList()
        );
    }

    @GetMapping(path = "/{sampleId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Sample not found.", content = @Content)
    public SampleResponse getSample(
            @PathVariable
            @Size(min = 36, max = 36, message = "sampleId length must be 36 characters.")
            String sampleId
    ) {
        var sample = sampleService.getSample(sampleId);
        return mapper.toResponse(sample);
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
                    description = "Project owner, Stage or some Fields not found.",
                    content = @Content
            )
    })
    public String createSample(@RequestBody @Valid SampleCreateRequest sampleCreateRequest) {
        return sampleService.createSample(sampleCreateRequest);
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
                    description = "Sample ID or Field ID is null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Answer not found.",
                    content = @Content
            )
    })
    public void updateAnswer(
            @PathVariable
            @Size(min = 36, max = 36, message = "sampleId length must be 36 characters.")
            String sampleId,
            @RequestBody @Valid AnswerUpsertRequest body
    ) {
        sampleService.updateAnswer(sampleId, body);
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
                    description = "Sample not found.",
                    content = @Content
            )
    })
    public void deleteSample(
            @PathVariable
            @Size(min = 36, max = 36, message = "sampleId length must be 36 characters.")
            String sampleId
    ) {
        sampleService.deleteSample(sampleId);
    }

}
