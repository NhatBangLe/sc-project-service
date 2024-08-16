package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.answer.Answer;
import com.microservices.projectservice.entity.answer.AnswerPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, AnswerPK> {
}