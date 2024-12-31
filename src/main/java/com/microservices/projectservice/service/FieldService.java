package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.DynamicFieldCreateRequest;
import com.microservices.projectservice.dto.request.DynamicFieldUpdateRequest;
import com.microservices.projectservice.dto.request.FieldCreateRequest;
import com.microservices.projectservice.dto.request.FieldUpdateRequest;
import com.microservices.projectservice.entity.DynamicField;
import com.microservices.projectservice.entity.Field;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.DynamicFieldRepository;
import com.microservices.projectservice.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    private final SampleService sampleService;
    private final FormService formService;

    public List<Field> getAllFields(String formId) throws NoEntityFoundException {
        var form = formService.getForm(formId);
        return fieldRepository.findAllByFormOrderByNumberOrderAsc(form);
    }

    public Field getField(String fieldId) throws NoEntityFoundException {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    public String createField(String formId, FieldCreateRequest body)
            throws NoEntityFoundException {
        var form = formService.getForm(formId);
        var field = Field.builder()
                .name(body.fieldName())
                .numberOrder(body.numberOrder())
                .form(form)
                .build();
        return fieldRepository.save(field).getId();
    }

    public void updateField(String fieldId, FieldUpdateRequest body)
            throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var field = getField(fieldId);

        var fieldName = body.fieldName();
        if (fieldName != null) {
            if (fieldName.isBlank())
                throw new IllegalAttributeException("Field name cannot be blank when updating a field.");
            field.setName(fieldName);
            isUpdated = true;
        }

        var numberOrder = body.numberOrder();
        if (numberOrder != null) {
            field.setNumberOrder(numberOrder);
            isUpdated = true;
        }

        if (isUpdated) fieldRepository.save(field);
    }

    public void deleteField(String fieldId) throws NoEntityFoundException {
        var field = getField(fieldId);
        fieldRepository.delete(field);
    }

    public String createDynamicField(String sampleId, DynamicFieldCreateRequest body)
            throws NoEntityFoundException {
        var sample = sampleService.getSample(sampleId);

        var numberOrder = body.numberOrder();
        if (numberOrder == null) numberOrder = 0;

        var dynamicField = DynamicField.builder()
                .name(body.name())
                .value(body.value())
                .numberOrder(numberOrder)
                .sample(sample)
                .build();
        return dynamicFieldRepository.save(dynamicField).getId();
    }

    public void updateDynamicField(String dynamicFieldId, DynamicFieldUpdateRequest body)
            throws IllegalAttributeException, NoEntityFoundException {
        var isUpdated = false;
        var dynamicField = findDynamicField(dynamicFieldId);

        var name = body.name();
        if (name != null) {
            if (name.isBlank())
                throw new IllegalAttributeException("Dynamic field name cannot be blank when updating a field.");
            dynamicField.setName(name);
            isUpdated = true;
        }

        var value = body.value();
        if (value != null) {
            dynamicField.setValue(value);
            isUpdated = true;
        }

        var numberOrder = body.numberOrder();
        if (numberOrder != null) {
            dynamicField.setNumberOrder(numberOrder);
            isUpdated = true;
        }

        if (isUpdated) dynamicFieldRepository.save(dynamicField);
    }

    public void deleteDynamicField(String dynamicFieldId) throws NoEntityFoundException {
        var dynamicField = findDynamicField(dynamicFieldId);
        dynamicFieldRepository.delete(dynamicField);
    }

    private DynamicField findDynamicField(String dynamicFieldId) throws NoEntityFoundException {
        return dynamicFieldRepository.findById(dynamicFieldId)
                .orElseThrow(() -> new NoEntityFoundException("No dynamic field found with id: " + dynamicFieldId));
    }

}
