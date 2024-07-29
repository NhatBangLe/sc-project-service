package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sample")
public class Sample {
    @Id
    @Column(nullable = false, length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


}