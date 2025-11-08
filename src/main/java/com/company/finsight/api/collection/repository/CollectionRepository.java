package com.company.finsight.api.collection.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.finsight.api.collection.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
