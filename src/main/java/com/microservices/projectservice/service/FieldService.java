package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.FieldCreateRequest;
import com.microservices.projectservice.dto.request.FieldUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.entity.Field;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.FieldRepository;
import com.microservices.projectservice.repository.FormRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;

    public List<FieldResponse> getAllFields(@NotNull String formId) {
        var form = findForm(formId);
        return fieldRepository.findAllByFormOrderByNumberOrderAsc(form).parallelStream()
                .map(this::mapFieldToResponse)
                .toList();
    }

    public FieldResponse getField(@NotNull String fieldId) {
        var field = findField(fieldId);
        return mapFieldToResponse(field);
    }

    public String createField(@NotNull String formId,
                              @NotNull FieldCreateRequest fieldCreateRequest)
            throws IllegalAttributeException, NoEntityFoundException {
        var form = findForm(formId);

        var fieldName = fieldCreateRequest.fieldName();
        if (fieldName == null || fieldName.isEmpty() || fieldName.isBlank())
            throw new IllegalAttributeException("Field name cannot be null/empty/blank");

        var numberOrder = fieldCreateRequest.numberOrder();
        if (numberOrder == null) numberOrder = 0;

        var field = Field.builder()
                .content(fieldName)
                .numberOrder(numberOrder)
                .form(form)
                .build();
        return fieldRepository.save(field).getId();
    }

    public void updateField(@NotNull String fieldId, @NotNull FieldUpdateRequest fieldUpdateRequest)
            throws IllegalAttributeException, NoEntityFoundException {
        var field = findField(fieldId);

        var fieldName = fieldUpdateRequest.fieldName();
        if (fieldName != null) {
            if (fieldName.isEmpty() || fieldName.isBlank())
                throw new IllegalAttributeException("Field name cannot be empty/blank");
            field.setContent(fieldName);
        }

        var numberOrder = fieldUpdateRequest.numberOrder();
        if (numberOrder != null) field.setNumberOrder(numberOrder);

        fieldRepository.save(field);
    }

    public void deleteField(@NotNull String fieldId) {
        var field = findField(fieldId);
        fieldRepository.delete(field);
    }

    private Field findField(@NotNull String fieldId) {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    private Form findForm(@NotNull String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    private FieldResponse mapFieldToResponse(@NotNull Field field) {
        return new FieldResponse(
                field.getId(),
                field.getNumberOrder(),
                field.getContent(),
                field.getForm().getId()
        );
    }

}
