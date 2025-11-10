package com.company.finsight.api.article.crawler.constant;

import lombok.Getter;

@Getter
public enum ArticleType {
    YNS("https://www.yna.co.kr"), HK("https://www.hankyung.com/");

    private final String baseUrl;

    ArticleType(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
