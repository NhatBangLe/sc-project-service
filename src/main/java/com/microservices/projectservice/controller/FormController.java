package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.request.FormCreateRequest;
import com.microservices.projectservice.dto.response.FormResponse;
import com.microservices.projectservice.dto.request.FormUpdateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/${API.VERSION}/form")
@RequiredArgsConstructor
@Tag(name = "Form", description = "All endpoints about forms.")
public class FormController {

    private final FormService formService;

    @GetMapping(path = "/{projectId}/project")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Get all forms own by project having the projectId.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page number or page size.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Project ID is not available.", content = @Content)
    })
    public PagingObjectsResponse<FormResponse> getAllForms(@PathVariable String projectId,
                                                           @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                           @RequestParam(required = false, defaultValue = "6") Integer pageSize) {
        return formService.getAllForms(projectId, pageNumber, pageSize);
    }

    @GetMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Form ID is not available.", content = @Content)
    public FormResponse getForm(@PathVariable String formId) {
        return formService.getForm(formId);
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
                    description = "Project owner ID or Form title is null/blank.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project owner ID is not available.",
                    content = @Content
            )
    })
    public String createForm(@RequestBody FormCreateRequest formCreateRequest) {
        return formService.createForm(formCreateRequest);
    }

    @PatchMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Form updated successfully."),
            @ApiResponse(responseCode = "400", description = "Form title is blank."),
            @ApiResponse(responseCode = "404", description = "Form ID is not available.")
    })
    public void updateForm(@PathVariable String formId, @RequestBody FormUpdateRequest formUpdateRequest) {
        formService.updateForm(formId, formUpdateRequest);
    }

    @DeleteMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Form deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Form ID is not available.")
    })
    public void deleteForm(@PathVariable String formId) {
        formService.deleteForm(formId);
    }

}
