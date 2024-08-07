package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.Sample;
import com.microservices.projectservice.entity.Stage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleRepository extends JpaRepository<Sample, String> {
    List<Sample> findAllByProjectOwnerOrderByCreatedTimestampAsc(Project project, Pageable pageable);
    List<Sample> findAllByStageOrderByCreatedTimestampAsc(Stage stage, Pageable pageable);
}