package com.company.finsight.api.article.crawler.parser;

import com.company.finsight.api.article.crawler.constant.ArticleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ArticleParserFactory {
    private final Map<ArticleType, ArticleParser> parserMap;

    public ArticleParserFactory(List<ArticleParser> parsers) {
        this.parserMap = parsers.stream()
                .collect(Collectors.toMap(ArticleParser::getSource, Function.identity()));
    }

    public ArticleParser getParser(ArticleType source) {
        ArticleParser parser = parserMap.get(source);
        if (parser == null) {
            throw new IllegalArgumentException("지원하지 않는 언론사입니다: " + source);
        }
        return parser;
    }
}

