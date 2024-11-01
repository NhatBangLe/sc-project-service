package com.microservices.projectservice.repository;

import com.microservices.projectservice.constant.ProjectStatus;
import com.microservices.projectservice.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, String> {

    Page<Project> findAllByOwner_IdAndStatus(String ownerId, ProjectStatus status, Pageable pageable);

    Page<Project> findAllByMembers_IdAndStatus(String userId, ProjectStatus status, Pageable pageable);

    @Query(
            value = """
                    select p from Project p
                    where p.id in (
                        select p.id from Project p
                        where p.owner.id = ?1 and p.status = ?2
                        union
                        select p.id from Project p join p.members m
                        where m.id = ?1 and p.owner.id != ?1 and p.status = ?2
                    )"""
    )
    Page<Project> findAllProjectByUserIdAndStatus(String userId, ProjectStatus status, Pageable pageable);

}