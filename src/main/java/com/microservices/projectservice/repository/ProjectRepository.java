package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Project;
import com.microservices.projectservice.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findAllByOwner(User owner, Pageable pageable);
}