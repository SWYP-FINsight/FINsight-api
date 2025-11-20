package com.company.finsight.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.finsight.api.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    // OAuth 로그인용 조회 메서드
    Optional<User> findByProviderId(String providerId);
}
