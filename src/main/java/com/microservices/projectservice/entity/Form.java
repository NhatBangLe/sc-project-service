package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FORM")
@EntityListeners(AuditingEntityListener.class)
public class Form extends AuditableEntity {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @OneToMany(mappedBy = "form")
    private Set<Stage> usageStages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_project_id", nullable = false, referencedColumnName = "id")
    private Project projectOwner;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private Set<Field> fields = new HashSet<>();
}