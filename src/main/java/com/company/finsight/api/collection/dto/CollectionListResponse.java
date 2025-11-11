package com.company.finsight.api.collection.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 모든 컬렉션 목록 조회 응답 DTO")
public class CollectionListResponse {

    @Schema(description = "컬렉션 정보 목록")
    private List<CollectionInfo> collections;

    @Schema(description = "전체 컬렉션 개수", example = "5")
    private int totalCount;

    private CollectionListResponse(List<CollectionInfo> collections, int totalCount) {
        this.collections = collections;
        this.totalCount = totalCount;
    }

    public static CollectionListResponse from(List<CollectionInfo> collections) {
        return new CollectionListResponse(
            collections,
            collections.size()
        );
    }
}
