package com.company.finsight.api.article.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_cid", unique = true, nullable = false)
    private String articleCid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "category")
    private String category;

    @Column(name = "reporter")
    private String reporter;

    @Column(name = "source")
    private String source;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "article_url", unique = true, nullable = false)
    private String articleUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Article() {}

    /**
     * Article 엔티티 생성 (정적 팩토리 메서드)
     *
     * @param articleCid 연합뉴스 기사 고유 ID
     * @param title 기사 제목
     * @param summary 기사 요약
     * @param content 기사 본문
     * @param category 카테고리
     * @param reporter 기자 이름
     * @param source 뉴스 출처
     * @param keywords 키워드
     * @param articleUrl 기사 원문 URL
     * @param thumbnailUrl 썸네일 이미지 URL
     * @param publishedAt 기사 발행 시간
     * @return Article 엔티티
     */
    public static Article create(
            String articleCid,
            String title,
            String summary,
            String content,
            String category,
            String reporter,
            String source,
            String keywords,
            String articleUrl,
            String thumbnailUrl,
            LocalDateTime publishedAt
    ) {
        Article article = new Article();
        article.articleCid = articleCid;
        article.title = title;
        article.summary = summary;
        article.content = content;
        article.category = category;
        article.reporter = reporter;
        article.source = source;
        article.keywords = keywords;
        article.articleUrl = articleUrl;
        article.thumbnailUrl = thumbnailUrl;
        article.publishedAt = publishedAt;
        return article;
    }

    /**
     * 필수 필드만으로 Article 엔티티 생성
     *
     * @param articleCid 연합뉴스 기사 고유 ID
     * @param title 기사 제목
     * @param articleUrl 기사 원문 URL
     * @param publishedAt 기사 발행 시간
     * @return Article 엔티티
     */
    public static Article createWithRequired(
            String articleCid,
            String title,
            String articleUrl,
            LocalDateTime publishedAt
    ) {
        return create(
                articleCid,
                title,
                null,
                null,
                null,
                null,
                null,
                null,
                articleUrl,
                null,
                publishedAt
        );
    }
}
