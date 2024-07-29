package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.FormCreateRequest;
import com.microservices.projectservice.dto.FormResponse;
import com.microservices.projectservice.dto.FormUpdateRequest;
import com.microservices.projectservice.service.FormService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/${api.version}/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @GetMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "404", description = "Form ID is not available")
    public FormResponse getForm(@PathVariable String formId) {
        return formService.getForm(formId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Project owner ID is not available"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project owner ID is null/empty/blank or Form title is null/empty/blank"
            )
    })
    public String createForm(@RequestBody FormCreateRequest formCreateRequest) {
        return formService.createForm(formCreateRequest);
    }

    @PatchMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateForm(@PathVariable String formId, @RequestBody FormUpdateRequest formUpdateRequest) {
        formService.updateForm(formId, formUpdateRequest);
    }

    @DeleteMapping(path = "/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "404", description = "Form ID is not available")
    public void deleteForm(@PathVariable String formId) {
        formService.deleteForm(formId);
    }

}
