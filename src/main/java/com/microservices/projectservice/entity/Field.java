package com.microservices.projectservice.entity;

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
@Table(name = "FIELD")
@EntityListeners(AuditingEntityListener.class)
public class Field extends AuditableEntity {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Builder.Default
    @Column(nullable = false)
    private Integer numberOrder = 0;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_form_id", nullable = false, updatable = false, referencedColumnName = "id")
    private Form form;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private Set<Answer> answers = new HashSet<>();
}