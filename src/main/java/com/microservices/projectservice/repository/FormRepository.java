package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Form;
import com.microservices.projectservice.entity.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormRepository extends JpaRepository<Form, String> {
    List<Form> findAllByProjectOwner(Project projectOwner, Pageable pageable);
}