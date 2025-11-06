package com.company.finsight.api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFilterDto {
    private String category;
    private String keyword;
    private LocalDate period;
    private String source;
}
