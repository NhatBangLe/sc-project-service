package com.microservices.projectservice.entity;

import com.microservices.projectservice.entity.embedded.AnswerPK;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "answer")
@EntityListeners(AuditingEntityListener.class)
public class Answer extends AuditableEntity {
    @EmbeddedId
    private AnswerPK primaryKey;

    @Column(nullable = false)
    private String value;

    @MapsId("fieldId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_field_id", updatable = false, nullable = false, referencedColumnName = "id")
    private Field field;

    @MapsId("sampleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sample_id", updatable = false, nullable = false, referencedColumnName = "id")
    private Sample sample;
}