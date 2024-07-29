package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, String> {
}