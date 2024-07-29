package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.FormCreateRequest;
import com.microservices.projectservice.dto.FormResponse;
import com.microservices.projectservice.dto.FormUpdateRequest;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Stage;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.ProjectRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final ProjectRepository projectRepository;

    public FormResponse getForm(@NotNull String formId) throws NoEntityFoundException {
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

    public String createForm(@NotNull FormCreateRequest formCreateRequest)
            throws IllegalAttributeException, NoEntityFoundException {
        var title = formCreateRequest.title();
        if (title == null || title.isEmpty() || title.isBlank())
            throw new IllegalAttributeException("Form title cannot be null/empty/blank");

        var projectOwnerId = formCreateRequest.projectOwnerId();
        if (projectOwnerId == null || projectOwnerId.isEmpty() || projectOwnerId.isBlank())
            throw new IllegalAttributeException("Project owner ID cannot be null/empty/blank");
        var project = projectRepository.findById(projectOwnerId)
                .orElseThrow(() -> new NoEntityFoundException("Project owner ID is not available, input ID: " + projectOwnerId));

        var form = Form.builder()
                .title(title)
                .description(formCreateRequest.description())
                .projectOwner(project)
                .build();
        return formRepository.save(form).getId();
    }

    public void updateForm(@NotNull String formId, @NotNull FormUpdateRequest formUpdateRequest)
            throws NoEntityFoundException, IllegalAttributeException {
        var form = findForm(formId);

        var title = formUpdateRequest.title();
        if (title != null) {
            if (title.isBlank() || title.isEmpty())
                throw new IllegalAttributeException("Form title cannot be null/empty/blank");
            form.setTitle(title);
        }

        var description = formUpdateRequest.description();
        if (description != null) form.setDescription(description);

        formRepository.save(form);
    }

    public void deleteForm(@NotNull String formId) throws NoEntityFoundException {
        var form = findForm(formId);
        formRepository.delete(form);
    }

    private Form findForm(@NotNull String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

}
