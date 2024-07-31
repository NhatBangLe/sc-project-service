package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.Stage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, String> {
    List<Stage> findAllByProjectOwner(Project projectOwner, Pageable pageable);
}