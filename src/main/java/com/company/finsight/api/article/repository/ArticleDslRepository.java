package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;

import java.time.LocalDate;
import java.util.List;

public interface ArticleDslRepository {
    List<Article> findByFilter(Long cursor, int size, String category, String search, LocalDate period, String source);
    List<String> findContentsByIdIn(List<Long> ids);
}
