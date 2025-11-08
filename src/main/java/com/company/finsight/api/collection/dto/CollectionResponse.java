package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.entity.PeriodType;

public record CollectionResponse(
    Long id,
    String collectionName,
    String keyword,
    PeriodType periodType,
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
