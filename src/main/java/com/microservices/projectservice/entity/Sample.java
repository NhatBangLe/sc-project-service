package com.microservices.projectservice.entity;

import com.microservices.projectservice.entity.answer.Answer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SAMPLE")
public class Sample {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String position;

    @Column(nullable = false)
    private String attachmentId;

    @CreationTimestamp
    private Timestamp createdTimestamp;

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