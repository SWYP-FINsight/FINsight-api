package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.entity.PeriodType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CollectionCond {
    private String keyword;
    private PeriodType periodType;
    private String source;

    private CollectionCond(String keyword, PeriodType periodType, String source) {
        this.keyword = keyword;
        this.periodType = periodType;
        this.source = source;
    }

    public static CollectionCond from(Collection collection) {
        return new CollectionCond(
            collection.getKeyword(),
            collection.getPeriodType(),
            collection.getSource()
        );
    }
}
