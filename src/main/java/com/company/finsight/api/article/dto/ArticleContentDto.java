package com.company.finsight.api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleContentDto {
    private String content;
    private String reporter;
}
