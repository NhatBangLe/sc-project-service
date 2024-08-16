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
@Table(name = "USER")
public class User {
    @Id
    @Column(length = 36)
    private String id;

    @OneToMany(mappedBy = "owner")
    private Set<Project> ownProjects = new HashSet<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private Set<Project> joinProjects = new HashSet<>();

}