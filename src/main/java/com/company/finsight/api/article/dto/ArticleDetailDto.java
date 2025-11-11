package com.company.finsight.api.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "기사 상세 조회 응답 DTO")
public class ArticleDetailDto {
    @Schema(description = "기사 ID", example = "1")
    private Long id;

    @Schema(description = "기사 제목", example = "한국은행, 기준금리 동결 결정")
    private String title;

    @Schema(description = "기사 출처", example = "연합뉴스")
    private String source;

    @Schema(description = "기사 발행 시간", example = "2024-07-29T09:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "기사 본문")
    private String content;

    @Schema(description = "기자 이름", example = "김기자")
    private String reporter;

    @Schema(description = "원본 기사 URL", example = "https://example.com/news/1")
    private String articleUrl;

    @Schema(description = "기사 중요도 (예: 높음, 중간, 낮음)", example = "높음")
    private String importance;
}
