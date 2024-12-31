package com.microservices.projectservice.service;

import com.microservices.projectservice.dto.request.AnswerUpsertRequest;
import com.microservices.projectservice.dto.request.SampleCreateRequest;
import com.microservices.projectservice.entity.*;
import com.microservices.projectservice.entity.Answer;
import com.microservices.projectservice.entity.embedded.AnswerPK;
import com.microservices.projectservice.exception.IllegalAttributeException;
import com.microservices.projectservice.exception.NoEntityFoundException;
import com.microservices.projectservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final SampleRepository sampleRepository;
    private final AnswerRepository answerRepository;
    private final DynamicFieldRepository dynamicFieldRepository;

    private final StageService stageService;
    private final FieldService fieldService;
    private final FileService fileService;

    @NonNull
    public Page<Sample> getAllSamplesByProjectId(String projectId, Integer pageNumber, Integer pageSize)
            throws NoEntityFoundException {
        if (!projectRepository.existsById(projectId))
            throw new NoEntityFoundException("No project found with id: " + projectId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return sampleRepository.findAllByProjectOwner_IdOrderByCreatedAtAsc(projectId, pageable);
    }

    public Page<Sample> getAllSamplesByStageId(String stageId, Integer pageNumber, Integer pageSize)
            throws NoEntityFoundException {
        if (!stageRepository.existsById(stageId))
            throw new NoEntityFoundException("No stage found with id: " + stageId);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return sampleRepository.findAllByStage_IdOrderByCreatedAtAsc(stageId, pageable);
    }

    public Sample getSample(String sampleId) throws NoEntityFoundException {
        return sampleRepository.findById(sampleId)
                .orElseThrow(() -> new NoEntityFoundException("No sample found with id: " + sampleId));
    }

    public String createSample(SampleCreateRequest body)
            throws NoEntityFoundException, IllegalAttributeException {
        var stage = stageService.getStage(body.stageId());
        var project = stage.getProjectOwner();

        // creating a sample
        var attachmentId = body.attachmentId();
        var isAttachmentExists = Objects.requireNonNull(
                fileService.checkFileExists(attachmentId),
                "Cannot check attachment existence for id: " + attachmentId
        );
        if (!isAttachmentExists)
            throw new IllegalAttributeException("No attachment found with id: " + attachmentId);
        var sample = sampleRepository.save(
                Sample.builder()
                        .attachmentId(attachmentId)
                        .position(body.position())
                        .projectOwner(project)
                        .stage(stage)
                        .build()
        );

        var answerUpsertRequests = body.answers();
        if (answerUpsertRequests != null) {
            var answers = answerUpsertRequests.stream().map(answerCreateRequest -> {
                        var field = fieldService.getField(answerCreateRequest.fieldId());
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

        var dynamicFieldCreateRequests = body.dynamicFields();
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

    public void updateAnswer(String sampleId, AnswerUpsertRequest answerUpsertRequest)
            throws NoEntityFoundException {
        String fieldId = answerUpsertRequest.fieldId(),
                value = answerUpsertRequest.value();
        var answerPK = new AnswerPK(sampleId, fieldId);
        var answer = answerRepository.findById(answerPK)
                .orElseThrow(() -> new NoEntityFoundException(
                        "Answer not found with sample ID: " + sampleId + " and field ID: " + fieldId));
        answer.setValue(value);
        answerRepository.save(answer);
    }

    public void deleteSample(String sampleId) throws NoEntityFoundException {
        var sample = getSample(sampleId);

        var attachmentId = sample.getAttachmentId();
        if (attachmentId != null) fileService.deleteFile(attachmentId);

        sampleRepository.delete(sample);
    }

}
