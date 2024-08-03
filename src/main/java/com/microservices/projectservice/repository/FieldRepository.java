package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Field;
import com.microservices.projectservice.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, String> {
    List<Field> findAllByFormOrderByNumberOrderAsc(Form form);
}