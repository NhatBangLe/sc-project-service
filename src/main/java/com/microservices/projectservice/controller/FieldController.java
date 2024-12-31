package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.DynamicFieldCreateRequest;
import com.microservices.projectservice.dto.request.DynamicFieldUpdateRequest;
import com.microservices.projectservice.dto.request.FieldCreateRequest;
import com.microservices.projectservice.dto.request.FieldUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.mapper.FieldMapper;
import com.microservices.projectservice.service.FieldService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/api/${app.api-version}/field")
@RequiredArgsConstructor
@Tag(name = "Field", description = "All endpoints about fields.")
public class FieldController {

    private final FieldService service;
    private final FieldMapper mapper;

    @GetMapping(path = "/{formId}/form")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Form ID is not available.", content = @Content)
    public List<FieldResponse> getAllFields(
            @PathVariable
            String formId
    ) {
        var fields = service.getAllFields(formId);
        return fields.stream().map(mapper::toResponse).toList();
    }

    @GetMapping(path = "/{fieldId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Field not found.", content = @Content)
    public FieldResponse getField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String fieldId
    ) {
        var field = service.getField(fieldId);
        return mapper.toResponse(field);
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
                    description = "Form not found.",
                    content = @Content
            )
    })
    public String createField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String formId,
            @RequestBody @Valid FieldCreateRequest body
    ) {
        return service.createField(formId, body);
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
                    description = "Field not found.",
                    content = @Content
            )
    })
    public void updateField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String fieldId,
            @RequestBody FieldUpdateRequest body) {
        service.updateField(fieldId, body);
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
                    description = "Field not found.",
                    content = @Content
            )
    })
    public void deleteField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String fieldId
    ) {
        service.deleteField(fieldId);
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
        return service.createDynamicField(sampleId, dynamicFieldCreateRequest);
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
                    description = "Field not found.",
                    content = @Content
            )
    })
    public void updateDynamicField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String fieldId,
            @RequestBody DynamicFieldUpdateRequest dynamicFieldUpdateRequest
    ) {
        service.updateDynamicField(fieldId, dynamicFieldUpdateRequest);
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
    public void deleteDynamicField(
            @PathVariable
            @Size(min = 36, max = 36, message = "fieldId length must be 36 characters.")
            String fieldId
    ) {
        service.deleteDynamicField(fieldId);
    }

}
