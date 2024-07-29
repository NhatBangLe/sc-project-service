package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, String> {
}