package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long>, ArticleDslRepository {
    Boolean existsByArticleCid(String articleCid);
}
