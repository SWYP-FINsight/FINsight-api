package com.company.finsight.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.finsight.api.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
}
