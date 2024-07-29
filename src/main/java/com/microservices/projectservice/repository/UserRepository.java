package com.microservices.projectservice.repository;

import com.microservices.projectservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}