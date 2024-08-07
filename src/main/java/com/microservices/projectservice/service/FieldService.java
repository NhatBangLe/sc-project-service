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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;
    private final SampleRepository sampleRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    public List<FieldResponse> getAllFields(@NotNull String formId) throws NoEntityFoundException {
        var form = findForm(formId);
        return fieldRepository.findAllByFormOrderByNumberOrderAsc(form).parallelStream()
                .map(this::mapFieldToResponse)
                .toList();
    }

    public FieldResponse getField(@NotNull String fieldId) throws NoEntityFoundException {
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
                .name(fieldName)
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
            field.setName(fieldName);
        }

        var numberOrder = fieldUpdateRequest.numberOrder();
        if (numberOrder != null) field.setNumberOrder(numberOrder);

        fieldRepository.save(field);
    }

    public void deleteField(@NotNull String fieldId) throws NoEntityFoundException {
        var field = findField(fieldId);
        fieldRepository.delete(field);
    }

    public String createDynamicField(
            @NotNull(message = "Sample ID cannot be null when updating a dynamic field") String sampleId,
            @NotNull(message = "The updating dynamic field data cannot be null")
            DynamicFieldCreateRequest dynamicFieldCreateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var sample = findSample(sampleId);

        var name = dynamicFieldCreateRequest.name();
        if (name == null || name.isEmpty() || name.isBlank())
            throw new IllegalAttributeException("Dynamic field name cannot be null/empty/blank");

        var value = dynamicFieldCreateRequest.value();
        if (value == null) value = "";

        var numberOrder = dynamicFieldCreateRequest.numberOrder();
        if (numberOrder == null) numberOrder = 0;

        var dynamicField = DynamicField.builder()
                .name(name)
                .value(value)
                .numberOrder(numberOrder)
                .sample(sample)
                .build();
        return dynamicFieldRepository.save(dynamicField).getId();
    }

    public void updateDynamicField(
            @NotNull(message = "Dynamic field ID cannot be null when updating a dynamic field") String dynamicFieldId,
            @NotNull(message = "The updating dynamic field data cannot be null")
            DynamicFieldUpdateRequest dynamicFieldUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        if (dynamicFieldId == null || dynamicFieldId.isBlank() || dynamicFieldId.isEmpty())
            throw new IllegalAttributeException("Dynamic field ID cannot be null/empty/blank");
        var dynamicField = findDynamicField(dynamicFieldId);

        var name = dynamicFieldUpdateRequest.name();
        if (name != null) {
            if (name.isEmpty() || name.isBlank())
                throw new IllegalAttributeException("Dynamic field name cannot be empty/blank");
            dynamicField.setName(name);
        }

        var value = dynamicFieldUpdateRequest.value();
        if (value != null) dynamicField.setValue(value);

        var numberOrder = dynamicFieldUpdateRequest.numberOrder();
        if (numberOrder != null) dynamicField.setNumberOrder(numberOrder);
    }

    public void deleteDynamicField(
            @NotNull(message = "Dynamic field ID cannot be null when updating a dynamic field") String dynamicFieldId
    ) throws IllegalAttributeException, NoEntityFoundException {
        if (dynamicFieldId == null || dynamicFieldId.isBlank() || dynamicFieldId.isEmpty())
            throw new IllegalAttributeException("Dynamic field ID cannot be null/empty/blank");
        var dynamicField = findDynamicField(dynamicFieldId);
        dynamicFieldRepository.delete(dynamicField);
    }

    private Field findField(@NotNull String fieldId) throws NoEntityFoundException {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    private DynamicField findDynamicField(@NotNull String dynamicFieldId)
            throws IllegalAttributeException, NoEntityFoundException {
        if (dynamicFieldId == null || dynamicFieldId.isEmpty() || dynamicFieldId.isBlank())
            throw new IllegalAttributeException("Dynamic Field ID cannot be null/empty/blank");
        return dynamicFieldRepository.findById(dynamicFieldId)
                .orElseThrow(() -> new NoEntityFoundException("No dynamic field found with id: " + dynamicFieldId));
    }

    private Form findForm(@NotNull String formId) throws NoEntityFoundException {
        return formRepository.findById(formId)
                .orElseThrow(() -> new NoEntityFoundException("No form found with id: " + formId));
    }

    private Sample findSample(@NotNull String sampleId) throws IllegalAttributeException, NoEntityFoundException {
        if (sampleId == null || sampleId.isEmpty() || sampleId.isBlank())
            throw new IllegalAttributeException("Sample ID cannot be null/empty/blank");
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new NoEntityFoundException("No sample found with id: " + sampleId));
    }

    private FieldResponse mapFieldToResponse(@NotNull Field field) {
        return new FieldResponse(
                field.getId(),
                field.getNumberOrder(),
                field.getName(),
                field.getForm().getId()
        );
    }

}
