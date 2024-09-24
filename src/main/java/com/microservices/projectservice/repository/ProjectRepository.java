package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, String> {

    List<Project> findAllByOwner_Id(String ownerId, Pageable pageable);

    List<Project> findAllByMembers_Id(String userId, Pageable pageable);

    @Query(
            value = """
                    SELECT DISTINCT * FROM project
                    WHERE id IN (SELECT * FROM
                            (SELECT id FROM project WHERE fk_owner_id = ?1) as owner
                                UNION
                            (SELECT project_id as id FROM project_member WHERE member_id = ?1)
                            )""",
            countQuery = """
                    SELECT count(*)
                    FROM (SELECT * FROM
                            (SELECT id FROM project WHERE fk_owner_id = ?1) as owner
                                UNION
                            (SELECT project_id as id FROM project_member WHERE member_id = ?1)
                            ) as count_id
                    """,
            nativeQuery = true
    )
    List<Project> findAllProjectByUserId(String userId, Pageable pageable);

}