package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article,Long>, ArticleDslRepository {
    Boolean existsByArticleCid(String articleCid);
    
    /**
     * 특정 출처(source)의 가장 최근 기사 조회
     */
    @Query("SELECT a FROM Article a WHERE a.source = :source ORDER BY a.publishedAt DESC LIMIT 1")
    Optional<Article> findLatestBySource(@Param("source") String source);
    
    /**
     * CID 목록으로 이미 존재하는 CID들만 조회 (배치 체크용)
     */
    @Query("SELECT a.articleCid FROM Article a WHERE a.articleCid IN :cids")
    List<String> findExistingCids(@Param("cids") List<String> cids);
    
    /**
     * 특정 날짜 이후의 기사 개수 조회
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.source = :source AND a.publishedAt > :afterDate")
    long countBySourceAndPublishedAtAfter(@Param("source") String source, @Param("afterDate") LocalDateTime afterDate);
}
