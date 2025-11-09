package com.company.finsight.api.collection.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CollectionListResponse {

    private List<CollectionInfo> collections;
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
