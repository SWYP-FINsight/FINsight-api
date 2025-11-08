package com.company.finsight.api.article.service;

import com.company.finsight.api.article.dto.ArticleContentDto;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ArticleParser {
    Mono<List<ArticleSummaryDto>> parseArticleList(String htmlContent, String categoryName, String baseUrl);
    Mono<ArticleContentDto> parseArticleContent(String htmlContent);
    ArticleType getSource();
}
