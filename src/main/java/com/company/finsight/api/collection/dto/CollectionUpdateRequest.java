package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.PeriodType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "컬렉션 수정 요청 DTO")
public class CollectionUpdateRequest {

    @Schema(description = "컬렉션 이름", example = "주식 시황")
    @NotBlank(message = "컬렉션 이름은 필수입니다.")
    private String collectionName;

    @Schema(description = "키워드", example = "삼성전자")
    private String keyword;

    @Schema(description = "기간 타입 (TODAY, WEEK, MONTH)", example = "WEEK")
    private PeriodType periodType;

    @Schema(description = "출처", example = "네이버 뉴스")
    private String source;
}
