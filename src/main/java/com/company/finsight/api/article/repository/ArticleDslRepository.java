package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ArticleDslRepository {
    Page<Article> findByFilter(Pageable pageable, String category, String keyword, LocalDate period, String source);
}
