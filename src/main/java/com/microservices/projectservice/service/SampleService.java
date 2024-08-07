package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.AnswerUpdateRequest;
import com.microservices.projectservice.dto.request.SampleCreateRequest;
import com.microservices.projectservice.dto.request.SampleUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.dto.response.SampleResponse;
import com.microservices.projectservice.entity.*;
import com.microservices.projectservice.entity.answer.Answer;
import com.microservices.projectservice.entity.answer.AnswerPK;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final SampleRepository sampleRepository;
    private final FieldRepository fieldRepository;
    private final AnswerRepository answerRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    public List<SampleResponse> getAllSamplesByProjectId(
            @NotNull(message = "Project ID cannot be null when finding all samples") String projectId,
            @NotNull(message = "Page number cannot be null when finding all samples") Integer pageNumber,
            @NotNull(message = "Page size cannot be null when finding all samples") Integer pageSize
    ) throws NoEntityFoundException {
        var project = findProject(projectId);
        var pageable = PageRequest.of(pageNumber, pageSize);

        return sampleRepository
                .findAllByProjectOwnerOrderByCreatedTimestampAsc(project, pageable)
                .parallelStream()
                .map(this::mapSampleToResponse)
                .toList();
    }

    public List<SampleResponse> getAllSamplesByStageId(
            @NotNull(message = "Stage ID cannot be null when finding all samples") String stageId,
            @NotNull(message = "Page number cannot be null when finding all samples") Integer pageNumber,
            @NotNull(message = "Page size cannot be null when finding all samples") Integer pageSize
    ) throws NoEntityFoundException {
        var stage = findStage(stageId);
        var pageable = PageRequest.of(pageNumber, pageSize);

        return sampleRepository
                .findAllByStageOrderByCreatedTimestampAsc(stage, pageable)
                .parallelStream()
                .map(this::mapSampleToResponse)
                .toList();
    }

    public SampleResponse getSample(
            @NotNull(message = "Sample ID cannot be null when finding a specific sample") String sampleId
    ) throws NoEntityFoundException {
        var sample = findSample(sampleId);
        return mapSampleToResponse(sample);
    }

    public String createSample(
            @NotNull(message = "The creating sample data cannot be null") SampleCreateRequest sampleCreateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var project = findProject(sampleCreateRequest.projectOwnerId());
        var stage = findStage(sampleCreateRequest.stageId());

        // mapping AnswerCreateRequest to Answer
        var answers = sampleCreateRequest.answers().stream()
                .map(answerCreateRequest -> {
                            var field = findField(answerCreateRequest.fieldId());
                            return Answer.builder()
                                    .value(answerCreateRequest.value())
                                    .field(field)
                                    .build();
                        }
                ).collect(Collectors.toSet());

        // mapping DynamicFieldCreateRequest to DynamicField
        var dynamicFields = sampleCreateRequest.dynamicFields().stream()
                .map(dynamicFieldCreateRequest -> DynamicField.builder()
                        .name(dynamicFieldCreateRequest.name())
                        .value(dynamicFieldCreateRequest.value())
                        .numberOrder(dynamicFieldCreateRequest.numberOrder())
                        .build()
                ).collect(Collectors.toSet());

        // creating a sample
        var sample = sampleRepository.save(
                Sample.builder()
                        .position(sampleCreateRequest.position())
                        .projectOwner(project)
                        .stage(stage)
                        .build()
        );

        // set sample for each answer and save them to database
        answers.forEach(answer -> answer.setSample(sample));
        answerRepository.saveAll(answers);

        // set dynamic fields for each field and save them to database
        dynamicFields.forEach(dynamicField -> dynamicField.setSample(sample));
        dynamicFieldRepository.saveAll(dynamicFields);

        return sample.getId();
    }

    public void updateSample(
            @NotNull(message = "Sample ID cannot be null when updating a specific sample") String sampleId,
            @NotNull(message = "The updating sample data cannot be null") SampleUpdateRequest sampleUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        var sample = findSample(sampleId);
        var position = sampleUpdateRequest.position();
        if (position != null) {
            sample.setPosition(position);
            sampleRepository.save(sample);
        }
    }

    public void updateAnswer(
            @NotNull(message = "Sample ID cannot be null when updating a answer") String sampleId,
            @NotNull(message = "Field ID cannot be null when updating a answer") String fieldId,
            @NotNull(message = "The updating answer data cannot be null")
            AnswerUpdateRequest answerUpdateRequest
    ) throws IllegalAttributeException, NoEntityFoundException {
        if (sampleId == null || sampleId.isBlank() || sampleId.isEmpty())
            throw new IllegalAttributeException("Sample ID cannot be null/empty/blank");
        if (fieldId == null || fieldId.isBlank() || fieldId.isEmpty())
            throw new IllegalAttributeException("Field ID cannot be null/empty/blank");

        var answerPK = new AnswerPK(sampleId, fieldId);
        var answer = answerRepository.findById(answerPK)
                .orElseThrow(() -> new NoEntityFoundException("Answer not found with sample ID: " + sampleId
                                                              + " and field ID: " + fieldId));
        var value = answerUpdateRequest.value();
        if (value == null) throw new IllegalAttributeException("Answer value cannot be null");
        answer.setValue(value);
        answerRepository.save(answer);
    }

    public void deleteSample(@NotNull String fieldId) throws NoEntityFoundException {
        var sample = findSample(fieldId);
        sampleRepository.delete(sample);
    }

    private Project findProject(@NotNull String projectId) throws IllegalAttributeException, NoEntityFoundException {
        if (projectId == null || projectId.isEmpty() || projectId.isBlank())
            throw new IllegalAttributeException("Project owner ID cannot be null/empty/blank");
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoEntityFoundException("No project found with id: " + projectId));
    }

    private Stage findStage(@NotNull String stageId) throws IllegalAttributeException, NoEntityFoundException {
        if (stageId == null || stageId.isEmpty() || stageId.isBlank())
            throw new IllegalAttributeException("Stage ID cannot be null/empty/blank");
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoEntityFoundException("No stage found with id: " + stageId));
    }

    private Field findField(@NotNull String fieldId) throws IllegalAttributeException, NoEntityFoundException {
        if (fieldId == null || fieldId.isEmpty() || fieldId.isBlank())
            throw new IllegalAttributeException("Field ID cannot be null/empty/blank");
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    private Sample findSample(@NotNull String sampleId) throws IllegalAttributeException, NoEntityFoundException {
        if (sampleId == null || sampleId.isEmpty() || sampleId.isBlank())
            throw new IllegalAttributeException("Sample ID cannot be null/empty/blank");
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new NoEntityFoundException("No sample found with id: " + sampleId));
    }

    private SampleResponse mapSampleToResponse(@NotNull Sample sample) {
        var answers = sample.getAnswers().stream()
                .map(answer -> {
                    var field = answer.getField();
                    var fieldResponse = new FieldResponse(
                            field.getId(),
                            field.getNumberOrder(),
                            field.getName(),
                            field.getForm().getId()
                    );
                    return new SampleResponse.AnswerResponse(
                            answer.getValue(),
                            fieldResponse
                    );
                })
                .toList();
        var dynamicFields = sample.getDynamicFields().stream()
                .map(dField -> new SampleResponse.DynamicFieldResponse(
                        dField.getId(),
                        dField.getName(),
                        dField.getValue(),
                        dField.getNumberOrder()
                ))
                .sorted(Comparator.comparingInt(SampleResponse.DynamicFieldResponse::numberOrder))
                .toList();

        return new SampleResponse(
                sample.getId(),
                sample.getPosition(),
                sample.getCreatedTimestamp(),
                sample.getProjectOwner().getId(),
                sample.getStage().getId(),
                answers,
                dynamicFields
        );
    }

}
