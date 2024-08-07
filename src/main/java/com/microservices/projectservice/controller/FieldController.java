package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.DynamicFieldCreateRequest;
import com.microservices.projectservice.dto.request.DynamicFieldUpdateRequest;
import com.microservices.projectservice.dto.request.FieldCreateRequest;
import com.microservices.projectservice.dto.request.FieldUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.service.FieldService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/${api.version}/field")
@RequiredArgsConstructor
@Tag(name = "Field", description = "All endpoints about fields.")
public class FieldController {

    private final FieldService fieldService;

    @GetMapping(path = "/{formId}/form")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Form ID is not available.", content = @Content)
    public List<FieldResponse> getAllFields(@PathVariable String formId) {
        return fieldService.getAllFields(formId);
    }

    @GetMapping(path = "/{fieldId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Field ID is not available.", content = @Content)
    public FieldResponse getField(@PathVariable String fieldId) {
        return fieldService.getField(fieldId);
    }

    @PostMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Field created successfully. Response: The ID of the created field."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Field name is null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Form ID is not available.",
                    content = @Content
            )
    })
    public String createField(@PathVariable String formId,
                              @RequestBody FieldCreateRequest fieldCreateRequest) {
        return fieldService.createField(formId, fieldCreateRequest);
    }

    @PatchMapping(path = "/{fieldId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Field updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Field name is blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field ID is not available.",
                    content = @Content
            )
    })
    public void updateField(@PathVariable String fieldId, @RequestBody FieldUpdateRequest fieldUpdateRequest) {
        fieldService.updateField(fieldId, fieldUpdateRequest);
    }

    @DeleteMapping(path = "/{fieldId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Field deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field ID is not available.",
                    content = @Content
            )
    })
    public void deleteField(@PathVariable String fieldId) {
        fieldService.deleteField(fieldId);
    }

    @PostMapping(path = "/{sampleId}/dynamic")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Dynamic field created successfully. Response: The ID of the created field."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Field name is null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sample ID is not available.",
                    content = @Content
            )
    })
    public String createDynamicField(@PathVariable String sampleId,
                                     @RequestBody DynamicFieldCreateRequest dynamicFieldCreateRequest) {
        return fieldService.createDynamicField(sampleId, dynamicFieldCreateRequest);
    }

    @PatchMapping(path = "/{fieldId}/dynamic")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Field updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Field ID or Field name is blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field ID is not available.",
                    content = @Content
            )
    })
    public void updateDynamicField(@PathVariable String fieldId,
                                   @RequestBody DynamicFieldUpdateRequest dynamicFieldUpdateRequest) {
        fieldService.updateDynamicField(fieldId, dynamicFieldUpdateRequest);
    }

    @DeleteMapping(path = "/{fieldId}/dynamic")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Dynamic Field deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field ID is not available.",
                    content = @Content
            )
    })
    public void deleteDynamicField(@PathVariable String fieldId) {
        fieldService.deleteDynamicField(fieldId);
    }

}
