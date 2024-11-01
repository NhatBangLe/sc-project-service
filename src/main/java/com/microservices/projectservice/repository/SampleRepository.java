package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, String> {
    Page<Sample> findAllByProjectOwner_IdOrderByCreatedAtAsc(String projectId, Pageable pageable);
    Page<Sample> findAllByStage_IdOrderByCreatedAtAsc(String stageId, Pageable pageable);
}