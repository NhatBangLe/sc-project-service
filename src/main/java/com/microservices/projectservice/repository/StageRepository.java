package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, String> {
    Page<Stage> findAllByProjectOwner_Id(String projectOwnerId, Pageable pageable);
    boolean existsByProjectOwner_IdAndMembers_Id(String userId, String memberId);
}