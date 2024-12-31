package com.microservices.projectservice.mapper;

import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.dto.response.SampleResponse;
import com.microservices.projectservice.entity.Sample;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SampleMapper implements IMapper<Sample, SampleResponse> {

    @Override
    public SampleResponse toResponse(Sample entity) {
        var answers = entity.getAnswers();
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
        var dynamicFields = entity.getDynamicFields();
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
                entity.getId(),
                entity.getAttachmentId(),
                entity.getPosition(),
                entity.getCreatedAt().getTime(),
                entity.getProjectOwner().getId(),
                entity.getStage().getId(),
                answerResponses,
                dynamicFieldResponses
        );
    }

}
