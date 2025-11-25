package com.company.finsight.api.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.finsight.api.ai.entity.AIArticle;

public interface AiArticleRepository extends JpaRepository<AIArticle, Long> {
}
