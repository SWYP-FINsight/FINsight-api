package com.company.finsight.api.collection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "컬렉션 목록에 포함될 개별 컬렉션 정보 DTO")
public class CollectionInfo {

    @Schema(description = "컬렉션 ID", example = "1")
    private Long id;

    @Schema(description = "컬렉션 이름", example = "나의 경제 뉴스")
    private String collectionName;
}
