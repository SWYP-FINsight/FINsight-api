package com.company.finsight.api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ArticleSummaryDto {
    private String articleCid;       // 연합뉴스 기사 ID
    private String title;            // 제목
    private String summary;          // 요약
    private String category;         // 카테고리 (여기서는 "산업/기업")
    private String source;           // 출처 (기본값: "연합뉴스")
    private String articleUrl;       // 기사 원문 URL
    private String thumbnailUrl;     // 썸네일 URL
    private LocalDateTime publishedAt; // 발행 시간
}