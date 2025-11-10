package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleDslRepository {
    Page<Article> findByFilter(Pageable pageable, String category, String keyword, LocalDate period, String source);
    List<String> findContentsByIdIn(List<Long> ids);
    
    /**
     * 특정 출처의 가장 최근 기사 조회
     */
    Optional<Article> findLatestBySource(String source);
    
    /**
     * CID 목록으로 이미 존재하는 CID들만 조회 (배치 체크용)
     */
    List<String> findExistingCids(List<String> cids);
    
    /**
     * 특정 날짜 이전의 기사 개수 조회
     */
    long countByPublishedAtBefore(LocalDateTime beforeDate);
    
    /**
     * 특정 날짜 이전의 기사 삭제
     */
    int deleteByPublishedAtBefore(LocalDateTime beforeDate);
}
