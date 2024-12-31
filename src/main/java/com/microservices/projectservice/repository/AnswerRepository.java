package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.Answer;
import com.microservices.projectservice.entity.embedded.AnswerPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, AnswerPK> {
}