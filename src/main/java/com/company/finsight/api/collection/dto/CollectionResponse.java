package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.entity.PeriodType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컬렉션 정보 응답 DTO")
public record CollectionResponse(
    @Schema(description = "컬렉션 ID", example = "1")
    Long id,
    @Schema(description = "컬렉션 이름", example = "나의 경제 뉴스")
    String collectionName,
    @Schema(description = "필터링 키워드", example = "기준금리")
    String keyword,
    @Schema(description = "필터링 기간", example = "TODAY, LAST_7_DAYS, LAST_30_DAYS")
    PeriodType periodType,
    @Schema(description = "필터링 출처", example = "한국경제")
    String source
) {
    public static CollectionResponse from(Collection collection) {
        return new CollectionResponse(
            collection.getId(),
            collection.getCollectionName(),
            collection.getKeyword(),
            collection.getPeriodType(),
            collection.getSource()
        );
    }
}
