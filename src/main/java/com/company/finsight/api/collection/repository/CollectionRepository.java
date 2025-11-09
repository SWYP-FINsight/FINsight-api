package com.company.finsight.api.collection.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.finsight.api.collection.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long>, CollectionDslRepository {
    Optional<Collection> findById(Long id);
}
