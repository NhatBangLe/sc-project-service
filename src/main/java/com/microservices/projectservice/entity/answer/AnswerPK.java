package com.microservices.projectservice.entity.answer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AnswerPK {
    private String sampleId;
    private String fieldId;
}
