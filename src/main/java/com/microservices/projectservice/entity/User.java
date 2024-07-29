package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> ownProjects = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "project_member",
            joinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Project> joinProjects = new HashSet<>();


}