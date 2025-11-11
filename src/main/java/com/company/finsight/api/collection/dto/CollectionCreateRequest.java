package com.company.finsight.api.collection.dto;

import com.company.finsight.api.collection.entity.PeriodType;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CollectionCreateRequest {

    @NotBlank(message = "컬렉션 이름은 필수입니다.")
    private String collectionName;

    private String keyword;  // nullable

    private PeriodType periodType;  // nullable

    private String source;  // nullable
}
