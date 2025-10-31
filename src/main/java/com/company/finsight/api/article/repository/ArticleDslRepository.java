package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import com.company.finsight.api.article.dto.ArticleFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface ArticleDslRepository {
    Page<Article> findByFilter(Pageable pageable, @Param("filter") ArticleFilterDto filterDto);
}
