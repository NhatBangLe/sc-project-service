package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.AnswerUpsertRequest;
import com.microservices.projectservice.dto.request.SampleCreateRequest;
import com.microservices.projectservice.dto.request.SampleUpdateRequest;
import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.dto.response.PagingObjectsResponse;
import com.microservices.projectservice.dto.response.SampleResponse;
import com.microservices.projectservice.entity.*;
import com.microservices.projectservice.entity.answer.Answer;
import com.microservices.projectservice.entity.answer.AnswerPK;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class SampleService {

    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final SampleRepository sampleRepository;
    private final FieldRepository fieldRepository;
    private final AnswerRepository answerRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    private final FileService fileService;

    public PagingObjectsResponse<SampleResponse> getAllSamplesByProjectId(
            @NotBlank(message = "Project ID cannot be null/blank when getting all samples.") String projectId,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all samples.")
            @NotNull(message = "Page number cannot be null when finding all samples.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all samples.")
            @NotNull(message = "Page size cannot be null when finding all samples.")
            Integer pageSize
    ) throws NoEntityFoundException {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        var samples = sampleRepository.findAllByProjectOwner_IdOrderByCreatedAtAsc(projectId, pageable);
        return new PagingObjectsResponse<>(
                samples.getTotalPages(),
                samples.getTotalElements(),
                samples.getNumber(),
                samples.getSize(),
                samples.getNumberOfElements(),
                samples.isFirst(),
                samples.isLast(),
                samples.map(this::mapSampleToResponse).toList()
        );
    }

    public PagingObjectsResponse<SampleResponse> getAllSamplesByStageId(
            @NotBlank(message = "Stage ID cannot be null/blank when getting all samples.") String stageId,
            @Min(value = 0, message = "Invalid page number (must positive) when getting all samples.")
            @NotNull(message = "Page number cannot be null when finding all samples.")
            Integer pageNumber,
            @Min(value = 1, message = "Invalid page size (must greater than 0) when getting all samples.")
            @NotNull(message = "Page size cannot be null when finding all samples.")
            Integer pageSize
    ) throws NoEntityFoundException {
        if (!stageRepository.existsById(stageId))
            throw new NoEntityFoundException("No stage found with id: " + stageId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        var samples = sampleRepository.findAllByStage_IdOrderByCreatedAtAsc(stageId, pageable);
        return new PagingObjectsResponse<>(
                samples.getTotalPages(),
                samples.getTotalElements(),
                samples.getNumber(),
                samples.getSize(),
                samples.getNumberOfElements(),
                samples.isFirst(),
                samples.isLast(),
                samples.map(this::mapSampleToResponse).toList()
        );
    }

    public SampleResponse getSample(
            @NotBlank(message = "Sample ID cannot be null/blank when getting a sample.") String sampleId
    ) throws NoEntityFoundException {
        var sample = findSample(sampleId);
        return mapSampleToResponse(sample);
    }

    public String createSample(
            @NotNull(message = "The creating sample data cannot be null.")
            @Valid
            SampleCreateRequest sampleCreateRequest
    ) throws NoEntityFoundException {
        var stage = findStage(sampleCreateRequest.stageId());
        var project = stage.getProjectOwner();

        // creating a sample
        var sample = sampleRepository.save(
                Sample.builder()
                        .attachmentId(sampleCreateRequest.attachmentId())
                        .position(sampleCreateRequest.position())
                        .projectOwner(project)
                        .stage(stage)
                        .build()
        );

        var answerUpsertRequests = sampleCreateRequest.answers();
        if (answerUpsertRequests != null) {
            var answers = answerUpsertRequests.stream().map(answerCreateRequest -> {
                        var field = findField(answerCreateRequest.fieldId());
                        var primaryKey = new AnswerPK(field.getId(), sample.getId());
                        return Answer.builder()
                                .primaryKey(primaryKey)
                                .value(answerCreateRequest.value())
                                .field(field)
                                .sample(sample)
                                .build();
                    }
            ).collect(Collectors.toSet());
            answerRepository.saveAll(answers);
        }

        var dynamicFieldCreateRequests = sampleCreateRequest.dynamicFields();
        if (dynamicFieldCreateRequests != null) {
            var dynamicFields = dynamicFieldCreateRequests.stream()
                    .map(dynamicFieldCreateRequest -> DynamicField.builder()
                            .name(dynamicFieldCreateRequest.name())
                            .value(dynamicFieldCreateRequest.value())
                            .numberOrder(dynamicFieldCreateRequest.numberOrder())
                            .sample(sample)
                            .build()
                    ).collect(Collectors.toSet());
            dynamicFieldRepository.saveAll(dynamicFields);
        }

        return sample.getId();
    }

    public void updateSample(
            @NotBlank(message = "Sample ID cannot be null when updating a sample.") String sampleId,
            @NotNull(message = "The updating sample data cannot be null.") SampleUpdateRequest sampleUpdateRequest
    ) throws NoEntityFoundException {
        var sample = findSample(sampleId);
        var isUpdated = false;

        var currentAttachmentId = sample.getAttachmentId();
        var newAttachmentId = sampleUpdateRequest.attachmentId();
        if (newAttachmentId != null) {
            if (newAttachmentId.isBlank())
                throw new IllegalAttributeException("Attachment ID cannot be blank when updating a field.");
            if (currentAttachmentId != null) fileService.deleteFile(currentAttachmentId);
            sample.setAttachmentId(newAttachmentId);
            isUpdated = true;
        }

        var position = sampleUpdateRequest.position();
        if (position != null) {
            sample.setPosition(position);
            isUpdated = true;
        }

        if (isUpdated) sampleRepository.save(sample);
    }

    public void updateAnswer(
            @NotBlank(message = "Sample ID cannot be null/blank when updating a answer.") String sampleId,
            @NotNull(message = "The updating answer data cannot be null.")
            @Valid
            AnswerUpsertRequest answerUpsertRequest
    ) throws NoEntityFoundException {
        String fieldId = answerUpsertRequest.fieldId(),
                value = answerUpsertRequest.value();
        var answerPK = new AnswerPK(sampleId, fieldId);
        var answer = answerRepository.findById(answerPK)
                .orElseThrow(() -> new NoEntityFoundException("Answer not found with sample ID: " + sampleId
                                                              + " and field ID: " + fieldId));
        answer.setValue(value);
        answerRepository.save(answer);
    }

    public void deleteSample(
            @NotBlank(message = "Sample ID cannot be null/blank when deleting a answer.") String sampleId
    ) throws NoEntityFoundException {
        var sample = findSample(sampleId);

        var attachmentId = sample.getAttachmentId();
        if (attachmentId != null) fileService.deleteFile(attachmentId);

        sampleRepository.delete(sample);
    }

    private Stage findStage(String stageId) throws NoEntityFoundException {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoEntityFoundException("No stage found with id: " + stageId));
    }

    private Field findField(String fieldId) throws NoEntityFoundException {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NoEntityFoundException("No field found with id: " + fieldId));
    }

    private Sample findSample(String sampleId) throws NoEntityFoundException {
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new NoEntityFoundException("No sample found with id: " + sampleId));
    }

    private SampleResponse mapSampleToResponse(Sample sample) {
        var answers = sample.getAnswers();
        List<SampleResponse.AnswerResponse> answerResponses = Collections.emptyList();
        if (answers != null)
            answerResponses = answers.stream()
                    .map(answer -> {
                        var field = answer.getField();
                        var fieldResponse = new FieldResponse(
                                field.getId(),
                                field.getNumberOrder(),
                                field.getName(),
                                field.getCreatedAt().getTime(),
                                field.getForm().getId()
                        );
                        return new SampleResponse.AnswerResponse(
                                answer.getValue(),
                                fieldResponse
                        );
                    })
                    .toList();
        var dynamicFields = sample.getDynamicFields();
        List<SampleResponse.DynamicFieldResponse> dynamicFieldResponses = Collections.emptyList();
        if (dynamicFields != null)
            dynamicFieldResponses = dynamicFields.stream()
                    .map(dField -> new SampleResponse.DynamicFieldResponse(
                            dField.getId(),
                            dField.getName(),
                            dField.getValue(),
                            dField.getNumberOrder(),
                            dField.getCreatedAt().getTime()
                    ))
                    .sorted(Comparator.comparingInt(SampleResponse.DynamicFieldResponse::numberOrder))
                    .toList();

        return new SampleResponse(
                sample.getId(),
                sample.getAttachmentId(),
                sample.getPosition(),
                sample.getCreatedAt().getTime(),
                sample.getProjectOwner().getId(),
                sample.getStage().getId(),
                answerResponses,
                dynamicFieldResponses
        );
    }

}
