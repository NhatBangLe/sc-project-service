package com.microservices.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DYNAMIC_FIELD")
@EntityListeners(AuditingEntityListener.class)
public class DynamicField extends AuditableEntity {
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