package com.microservices.projectservice.entity.answer;

import com.microservices.projectservice.entity.Field;
import com.microservices.projectservice.entity.Sample;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "answer")
public class Answer {
    @EmbeddedId
    private AnswerPK primaryKey;

    @Column(nullable = false)
    private String value;

    @MapsId("fieldId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_field_id", nullable = false, referencedColumnName = "id")
    private Field field;

    @MapsId("sampleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sample_id", nullable = false, referencedColumnName = "id")
    private Sample sample;
}