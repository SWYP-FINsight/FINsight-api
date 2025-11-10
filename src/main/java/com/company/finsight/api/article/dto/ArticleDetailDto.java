package com.company.finsight.api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 기사 상세 조회 응답 DTO
 */
@Getter
@AllArgsConstructor
public class ArticleDetailDto {
    private Long id;
    private String title;
    private String source;
    private LocalDateTime timestamp;
    private String content;
    private String reporter;
    private String articleUrl;
    private String importance;
}