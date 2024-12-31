package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.FormCreateRequest;
import com.microservices.projectservice.dto.response.FormResponse;
import com.microservices.projectservice.dto.request.FormUpdateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.mapper.FormMapper;
import com.microservices.projectservice.service.FormService;
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
@RequestMapping(path = "/api/${app.api-version}/form")
@RequiredArgsConstructor
@Tag(name = "Form", description = "All endpoints about forms.")
public class FormController {

    private final FormService formService;
    private final FormMapper mapper;

    @GetMapping(path = "/{projectId}/project")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get all forms own by a project having the projectId.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page number or page size.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Project not found.", content = @Content)
    })
    public PagingObjectsResponse<FormResponse> getAllForms(
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
        var forms = formService.getAllForms(projectId, pageNumber, pageSize);
        var response = new PagingObjectsResponse<>(
                forms.getTotalPages(),
                forms.getTotalElements(),
                forms.getNumber(),
                forms.getSize(),
                forms.getNumberOfElements(),
                forms.isFirst(),
                forms.isLast(),
                forms.toList()
        );
        return response.map(mapper::toResponse);
    }

    @GetMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid formId.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Form not found.",
                    content = @Content
            )
    })
    public FormResponse getForm(
            @PathVariable
            @Size(min = 36, max = 36, message = "formId length must be 36 characters.")
            String formId
    ) {
        var form = formService.getForm(formId);
        return mapper.toResponse(form);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Form created successfully. Response: The ID of the created form."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid creating properties.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project owner not found.",
                    content = @Content
            )
    })
    public String createForm(@RequestBody @Valid FormCreateRequest formCreateRequest) {
        return formService.createForm(formCreateRequest);
    }

    @PatchMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Form updated successfully."),
            @ApiResponse(responseCode = "400", description = "Form title is blank."),
            @ApiResponse(responseCode = "404", description = "Form ID is not available.")
    })
    public void updateForm(
            @PathVariable
            @Size(min = 36, max = 36, message = "formId length must be 36 characters.")
            String formId,
            @RequestBody FormUpdateRequest formUpdateRequest
    ) {
        formService.updateForm(formId, formUpdateRequest);
    }

    @DeleteMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Form deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Form is being used by several stages."),
            @ApiResponse(responseCode = "404", description = "Form not found.")
    })
    public void deleteForm(
            @PathVariable
            @Size(min = 36, max = 36, message = "formId length must be 36 characters.")
            String formId
    ) {
        formService.deleteForm(formId);
    }

}
