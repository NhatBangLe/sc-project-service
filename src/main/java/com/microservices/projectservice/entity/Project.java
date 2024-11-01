package com.microservices.projectservice.entity;

import com.microservices.projectservice.constant.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROJECT")
@EntityListeners(AuditingEntityListener.class)
public class Project extends AuditableEntity {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String thumbnailId;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "smallint default 0")
    private ProjectStatus status;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "fk_owner_id", nullable = false, referencedColumnName = "id")
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "project_member",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    )
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Form> forms = new HashSet<>();

    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Stage> stages = new HashSet<>();

    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Sample> samples = new HashSet<>();
}