package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAGE")
@EntityListeners(AuditingEntityListener.class)
public class Stage extends AuditableEntity {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "stage_member",
            joinColumns = @JoinColumn(name = "stage_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    )
    private Set<User> members = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_form_id", referencedColumnName = "id")
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_project_id", nullable = false, referencedColumnName = "id")
    private Project projectOwner;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Sample> samples;
}