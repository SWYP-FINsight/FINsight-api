package com.company.finsight.api.article.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ArticleFilterDto {
    private String category;
    private String keyword;
    private LocalDate period;
    private String source;
}
