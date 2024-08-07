package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.DynamicFieldCreateRequest;
import com.microservices.projectservice.dto.request.DynamicFieldUpdateRequest;
import com.microservices.projectservice.dto.request.FieldCreateRequest;
import com.microservices.projectservice.dto.request.FieldUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.entity.DynamicField;
import com.microservices.projectservice.entity.Field;
import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Sample;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.DynamicFieldRepository;
import com.microservices.projectservice.repository.FieldRepository;
import com.microservices.projectservice.repository.FormRepository;
import com.microservices.projectservice.repository.SampleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;
    private final SampleRepository sampleRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    public List<FieldResponse> getAllFields(
            @NotBlank(message = "Form ID cannot be null/blank when getting all fields.") String formId
    ) throws NoEntityFoundException {
        var form = findForm(formId);
        return fieldRepository.findAllByFormOrderByNumberOrderAsc(form).parallelStream()
                .map(this::mapFieldToResponse)
                .toList();
    }

    public FieldResponse getField(
            @NotBlank(message = "Field ID cannot be null/blank when getting a field.") String fieldId
    ) throws NoEntityFoundException {
        var field = findField(fieldId);
        return mapFieldToResponse(field);
    }

    public String createField(
            @NotBlank(message = "Form ID cannot be null/blank when creating a field.") String formId,
            @NotNull(message = "Creating field data cannot be null.")
            @Valid
            FieldCreateRequest fieldCreateRequest
    ) throws NoEntityFoundException {
        var form = findForm(formId);
        var numberOrder = fieldCreateRequest.numberOrder();
        if (numberOrder == null) numberOrder = 0;

        var field = Field.builder()
                .name(fieldCreateRequest.fieldName())
                .numberOrder(numberOrder)
                .form(form)
                .build();
        return fieldRepository.save(field).getId();
    }

    public void updateField(
            @NotBlank(message = "Field ID cannot be null/blank when updating a field.") String fieldId,
            @NotNull(message = "Updating field data cannot be null.") FieldUpdateRequest fieldUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var field = findField(fieldId);

        var fieldName = fieldUpdateRequest.fieldName();
        if (fieldName != null) {
            if (fieldName.isBlank())
                throw new IllegalAttributeException("Field name cannot be blank when updating a field.");
            field.setName(fieldName);
            isUpdated = true;
        }

        var numberOrder = fieldUpdateRequest.numberOrder();
        if (numberOrder != null) {
            field.setNumberOrder(numberOrder);
            isUpdated = true;
        }

        if (isUpdated) fieldRepository.save(field);
    }

    public void deleteField(
            @NotBlank(message = "Field ID cannot be null/blank when deleting a field.") String fieldId
    ) throws NoEntityFoundException {
        var field = findField(fieldId);
        fieldRepository.delete(field);
    }

    public String createDynamicField(
            @NotBlank(message = "Sample ID cannot be null/blank when creating a dynamic field.") String sampleId,
            @NotNull(message = "Creating dynamic field data cannot be null.")
            @Valid
            DynamicFieldCreateRequest dynamicFieldCreateRequest
    ) throws NoEntityFoundException {
        var sample = findSample(sampleId);

        var dynamicField = DynamicField.builder()
                .name(dynamicFieldCreateRequest.name())
                .value(dynamicFieldCreateRequest.value())
                .numberOrder(dynamicFieldCreateRequest.numberOrder())
                .sample(sample)
                .build();
        return dynamicFieldRepository.save(dynamicField).getId();
    }

    public void updateDynamicField(
            @NotBlank(message = "Dynamic field ID cannot be null/blank when updating a field.") String dynamicFieldId,
            @NotNull(message = "Updating dynamic field data cannot be null.")
            @Valid
            DynamicFieldUpdateRequest dynamicFieldUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var dynamicField = findDynamicField(dynamicFieldId);

        var name = dynamicFieldUpdateRequest.name();
        if (name != null) {
            if (name.isBlank())
                throw new IllegalAttributeException("Dynamic field name cannot be blank when updating a field.");
            dynamicField.setName(name);
            isUpdated = true;
        }

        var value = dynamicFieldUpdateRequest.value();
        if (value != null) {
            dynamicField.setValue(value);
            isUpdated = true;
        }

        var numberOrder = dynamicFieldUpdateRequest.numberOrder();
        if (numberOrder != null) {
            dynamicField.setNumberOrder(numberOrder);
            isUpdated = true;
        }

        if (isUpdated) dynamicFieldRepository.save(dynamicField);
    }

    public void deleteDynamicField(
            @NotBlank(message = "Dynamic field ID cannot be null/blank when deleting a field.") String dynamicFieldId
    ) throws NoEntityFoundException {
        var dynamicField = findDynamicField(dynamicFieldId);
        dynamicFieldRepository.delete(dynamicField);
    }

    private Field findField(String fieldId) throws NoEntityFoundException {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    private DynamicField findDynamicField(String dynamicFieldId) throws NoEntityFoundException {
        return dynamicFieldRepository.findById(dynamicFieldId)
                .orElseThrow(() -> new NoEntityFoundException("No dynamic field found with id: " + dynamicFieldId));
    }

    private Form findForm(String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    private Sample findSample(String sampleId) throws NoEntityFoundException {
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new NoEntityFoundException("No sample found with id: " + sampleId));
    }

    private FieldResponse mapFieldToResponse(Field field) {
        return new FieldResponse(
                field.getId(),
                field.getNumberOrder(),
                field.getName(),
                field.getForm().getId()
        );
    }

}
