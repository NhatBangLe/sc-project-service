package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, String> {
    Page<Form> findAllByProjectOwner_Id(String projectOwnerId, Pageable pageable);
}