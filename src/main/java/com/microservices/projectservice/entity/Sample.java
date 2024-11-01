package com.microservices.projectservice.entity;

import com.microservices.projectservice.entity.answer.Answer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SAMPLE")
@EntityListeners(AuditingEntityListener.class)
public class Sample extends AuditableEntity {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String position;

    @Column(nullable = false)
    private String attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_project_id", nullable = false, referencedColumnName = "id")
    private Project projectOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_stage_id", nullable = false, referencedColumnName = "id")
    private Stage stage;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private Set<DynamicField> dynamicFields = new HashSet<>();
}