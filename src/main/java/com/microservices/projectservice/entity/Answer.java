package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "answer",
        uniqueConstraints = @UniqueConstraint(
                name = "CK_FIELD_ID_AND_SAMPLE_ID",
                columnNames = {"fk_field_id", "fk_sample_id"}
        )
)
public class Answer {
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_field_id", nullable = false, referencedColumnName = "id")
    private Field field;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sample_id", nullable = false, referencedColumnName = "id")
    private Sample sample;
}