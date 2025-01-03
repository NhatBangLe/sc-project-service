package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USER")
@EntityListeners(AuditingEntityListener.class)
public class User extends AuditableEntity {
    @Id
    @Column(length = 36)
    private String id;

    @OneToMany(mappedBy = "owner")
    private Set<Project> ownProjects = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    private Set<Project> joinProjects = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    private Set<Stage> joinStages = new HashSet<>();
}