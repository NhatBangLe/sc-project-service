package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.DynamicField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicFieldRepository extends JpaRepository<DynamicField, String> {
}