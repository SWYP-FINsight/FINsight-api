package com.company.finsight.api.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "기사 목록 조회 응답 DTO")
public class ArticlesDto {
    @Schema(description = "기사 ID", example = "1")
    private Long  id;

    @Schema(description = "기사 제목", example = "한국은행, 기준금리 동결 결정")
    private String title;

    @Schema(description = "기사 출처", example = "연합뉴스")
    private String source;

    @Schema(description = "기사 발행 시간", example = "2024-07-29T09:00:00")
    private LocalDateTime timestamp;
}
