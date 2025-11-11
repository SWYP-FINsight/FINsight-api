package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.PeriodType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "새 컬렉션 생성 요청 DTO")
public class CollectionCreateRequest {

    @Schema(description = "컬렉션 이름", example = "나의 경제 뉴스")
    @NotBlank(message = "컬렉션 이름은 필수입니다.")
    private String collectionName;

    @Schema(description = "필터링할 키워드 (선택)", example = "기준금리")
    private String keyword;

    @Schema(description = "필터링할 기간 (선택)", example = "TODAY, LAST_7_DAYS, LAST_30_DAYS")
    private PeriodType periodType;

    @Schema(description = "필터링할 출처 (선택)", example = "한국경제")
    private String source;
}
