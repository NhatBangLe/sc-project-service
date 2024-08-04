package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DYNAMIC_FIELD")
public class DynamicField {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer numberOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sample_id", nullable = false, referencedColumnName = "id")
    private Sample sample;
}