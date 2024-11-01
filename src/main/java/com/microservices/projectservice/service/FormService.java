package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.FormCreateRequest;
import com.microservices.projectservice.dto.response.FormResponse;
import com.microservices.projectservice.dto.request.FormUpdateRequest;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.ProjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final ProjectRepository projectRepository;

    public PagingObjectsResponse<FormResponse> getAllForms(
            @NotBlank(message = "Project ID cannot be null/blank when getting all forms.") String projectId,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all forms.")
            @NotNull(message = "Page number cannot be null when getting all forms.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all forms.")
            @NotNull(message = "Page size cannot be null when getting all forms.")
            Integer pageSize
    ) throws NoEntityFoundException {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize);
        var forms = formRepository.findAllByProjectOwner_Id(projectId, pageable)
                .map(form -> {
                    var usageStageIds = form.getUsageStages().parallelStream().map(Stage::getId).toList();
                    return new FormResponse(
                            form.getId(),
                            form.getTitle(),
                            form.getDescription(),
                            form.getProjectOwner().getId(),
                            usageStageIds
                    );
                });
        return new PagingObjectsResponse<>(
                forms.getTotalPages(),
                forms.getTotalElements(),
                forms.getNumber(),
                forms.getSize(),
                forms.getNumberOfElements(),
                forms.isFirst(),
                forms.isLast(),
                forms.toList()
        );
    }

    public FormResponse getForm(
            @NotBlank(message = "Form ID cannot be null/blank when getting a form.") String formId
    ) throws NoEntityFoundException {
        var form = findForm(formId);
        var usageStageIds = form.getUsageStages().stream().map(Stage::getId).toList();

        return new FormResponse(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getProjectOwner().getId(),
                usageStageIds
        );
    }

    public String createForm(
            @NotNull(message = "Creating form data cannot be null.")
            @Valid
            FormCreateRequest formCreateRequest
    ) throws NoEntityFoundException {
        var project = findProject(formCreateRequest.projectOwnerId());
        var form = Form.builder()
                .title(formCreateRequest.title())
                .description(formCreateRequest.description())
                .projectOwner(project)
                .build();
        return formRepository.save(form).getId();
    }

    public void updateForm(
            @NotBlank(message = "Form ID cannot be null/blank when updating a form.") String formId,
            @NotNull(message = "Updating form data cannot be null.")
            @Valid
            FormUpdateRequest formUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var isUpdate = false;
        var form = findForm(formId);

        var title = formUpdateRequest.title();
        if (title != null) {
            if (title.isBlank()) throw new IllegalAttributeException("Form title cannot be blank.");
            form.setTitle(title);
            isUpdate = true;
        }

        var description = formUpdateRequest.description();
        if (description != null) {
            form.setDescription(description);
            isUpdate = true;
        }

        if (isUpdate) formRepository.save(form);
    }

    public void deleteForm(
            @NotBlank(message = "Form ID cannot be null/blank when updating a form.") String formId
    ) throws NoEntityFoundException {
        var form = findForm(formId);
        formRepository.delete(form);
    }

    private Form findForm(String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    private Project findProject(String projectId) throws NoEntityFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

}
