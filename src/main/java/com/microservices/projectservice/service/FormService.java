package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.FormCreateRequest;
import com.microservices.projectservice.dto.request.FormUpdateRequest;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public Page<Form> getAllForms(String projectId,
                                  Integer pageNumber,
                                  Integer pageSize) throws NoEntityFoundException {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return formRepository.findAllByProjectOwner_Id(projectId, pageable);
    }

    public Form getForm(String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    public String createForm(FormCreateRequest body) throws NoEntityFoundException {
        var project = projectService.getProject(body.projectOwnerId());
        var form = Form.builder()
                .title(body.title())
                .description(body.description())
                .projectOwner(project)
                .build();
        return formRepository.save(form).getId();
    }

    public void updateForm(String formId, FormUpdateRequest body)
            throws IllegalAttributeException, NoEntityFoundException {
        var isUpdate = false;
        var form = getForm(formId);

        var title = body.title();
        if (title != null) {
            if (title.isBlank()) throw new IllegalAttributeException("title cannot be blank.");
            form.setTitle(title);
            isUpdate = true;
        }

        var description = body.description();
        if (description != null) {
            form.setDescription(description);
            isUpdate = true;
        }

        if (isUpdate) formRepository.save(form);
    }

    public void deleteForm(String formId) throws NoEntityFoundException, IllegalAttributeException {
        var form = getForm(formId);
        var usageStages = Objects.requireNonNullElse(form.getUsageStages(), Collections.emptyList());
        if (!usageStages.isEmpty()) throw new IllegalAttributeException(
                "The form cannot be deleted. Because, currently, it is being used by several stages.");
        formRepository.delete(form);
    }

}
