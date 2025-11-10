package com.company.finsight.api.article.crawler.parser;

import com.company.finsight.api.article.crawler.constant.ArticleType;
import com.company.finsight.api.article.dto.ArticleContentDto;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HKArticleParser implements ArticleParser {

    @Override
    public Mono<List<ArticleSummaryDto>> parseArticleList(String xmlContent, String categoryName, String baseUrl) {
        return Mono.fromCallable(() -> {
            List<ArticleSummaryDto> articleDtoList = new ArrayList<>();
            Document doc = Jsoup.parse(xmlContent, "", org.jsoup.parser.Parser.xmlParser());
            Elements urlElements = doc.select("url");

            log.info("{}개의 아티클을 찾았습니다. 카테고리 : '{}'", urlElements.size(), categoryName);
            
            for (Element urlElement : urlElements) {
                try {
                    // 기본 정보 추출
                    Element locElement = urlElement.selectFirst("loc");
                    Element newsElement = urlElement.selectFirst("news|news");
                    Element imageElement = urlElement.selectFirst("image|image");
                    
                    if (locElement == null || newsElement == null) {
                        log.warn("필수 요소가 없습니다. loc 또는 news");
                        continue;
                    }

                    String articleUrl = locElement.text();
                    String articleCid = extractArticleId(articleUrl);
                    
                    // 뉴스 정보 추출
                    Element titleElement = newsElement.selectFirst("news|title");
                    Element pubDateElement = newsElement.selectFirst("news|publication_date");
                    
                    if (titleElement == null || pubDateElement == null) {
                        log.warn("필수 뉴스 정보가 없습니다. CID: {}", articleCid);
                        continue;
                    }

                    String title = titleElement.text();
                    String pubDateStr = pubDateElement.text();
                    LocalDateTime publishedAt = parseDateTime(pubDateStr);
                    
                    // 썸네일 추출
                    String thumbnailUrl = null;
                    if (imageElement != null) {
                        Element imageLocElement = imageElement.selectFirst("image|loc");
                        if (imageLocElement != null) {
                            thumbnailUrl = imageLocElement.text();
                        }
                    }

                    ArticleSummaryDto dto = ArticleSummaryDto.builder()
                            .articleCid(articleCid)
                            .title(title)
                            .summary("")
                            .category(categoryName)
                            .source("한국경제")
                            .articleUrl(articleUrl.replace(baseUrl, ""))
                            .thumbnailUrl(thumbnailUrl)
                            .publishedAt(publishedAt)
                            .build();
                    
                    articleDtoList.add(dto);
                    log.debug("Parsed article: {}", dto);
                    
                } catch (Exception e) {
                    log.error("파싱 실패 : {}", urlElement.html(), e);
                }
            }
            
            log.info("파싱 성공 개수 {} 카테고리 : {}", articleDtoList.size(), categoryName);
            return articleDtoList;
        });
    }

    @Override
    public Mono<ArticleContentDto> parseArticleContent(String htmlContent) {
        return Mono.fromCallable(() -> {
            Document doc = Jsoup.parse(htmlContent);
            
            // 본문 추출
            Element articleBodyElement = doc.selectFirst("div.article-body#articletxt");
            String content = "";
            
            if (articleBodyElement != null) {
                content = articleBodyElement.text();
                
                // 불필요한 문구 제거
                content = content.replaceFirst("^\\s*\\([^)]+\\)\\s*", "").trim();
                
                // 기자명 패턴 제거 (예: "신현보 한경닷컴 기자 greaterfool@hankyung.com")
                content = content.replaceAll("\\s*[가-힣]+\\s+한경닷컴\\s+기자\\s+[a-zA-Z0-9_]+@hankyung\\.com\\s*$", "").trim();
                
                // 저작권 문구 제거
                int copyrightIndex = content.indexOf("한국경제신문");
                if (copyrightIndex != -1) {
                    content = content.substring(0, copyrightIndex).trim();
                }
            } else {
                log.warn("Article body element not found.");
            }

            // 기자 이름
            Element reporterElement = doc.selectFirst("div.author-list div.author a.item");
            String reporter = "";
            
            if (reporterElement != null) {
                reporter = reporterElement.text();
            } else {
                log.info("작성자 파악 불가");
                // JSON-LD 구조에서 추출 시도
                Element scriptElement = doc.selectFirst("script[type='application/ld+json']");
                if (scriptElement != null) {
                    String jsonText = scriptElement.html();
                    // 간단한 정규식으로 name 추출
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(jsonText);
                    if (matcher.find()) {
                        reporter = matcher.group(1);
                    }
                }
            }

            if (!content.isEmpty() || !reporter.isEmpty()) {
                return new ArticleContentDto(content, reporter);
            } else {
                log.warn("본문, 작성자 파싱 실패.");
                return null;
            }
        });
    }

    @Override
    public ArticleType getSource() {
        return ArticleType.HK;
    }
    
    /**
     * URL에서 기사 ID 추출
     * 예: https://www.hankyung.com/article/202511103488i -> 202511103488i
     */
    private String extractArticleId(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
    
    /**
     * ISO 8601 날짜 형식 파싱
     * 예: 2025-11-10T12:53:55+09:00
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.warn("시간 변환 실패: {}", dateTimeStr, e);
            return null;
        }
    }
}
