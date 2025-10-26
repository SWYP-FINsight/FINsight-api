package com.company.finsight.api.article.service;

import com.company.finsight.api.article.dto.ArticleDto;
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
public class ArticleParser {

    public Mono<List<ArticleDto>> parseArticleList(String htmlContent, String categoryName) {
        return Mono.fromCallable(() -> {
            List<ArticleDto> articleDtoList = new ArrayList<>();
            Document doc = Jsoup.parse(htmlContent);
            Elements articleElements = doc.select("div.list-type212 > ul.list01 > li");

            log.info("{}개의 아티클을 찾았습니다. 카테고리 : '{}'", articleElements.size(), categoryName);
            for (Element articleElement : articleElements) {
                if (articleElement.hasAttr("data-cid")) {
                    try {
                        String cid = articleElement.attr("data-cid");
                        Element titleLink = articleElement.selectFirst("a.tit-news");
                        Element summaryElement = articleElement.selectFirst("p.lead");
                        Element timeElement = articleElement.selectFirst("span.txt-time");
                        Element imgElement = articleElement.selectFirst("figure.img-con01 img"); // 이미지 태그 선택

                        if (titleLink != null && summaryElement != null && timeElement != null) {
                            String title = titleLink.text();
                            String fullUrl = titleLink.absUrl("href");
                            String summary = summaryElement.text();
                            String timeStr = timeElement.text(); // 예: "10-26 17:18"
                            String thumbnailUrl = (imgElement != null) ? imgElement.attr("src") : null; // 썸네일 URL 추출

                            // 시간 파싱
                            LocalDateTime publishedAt = parseApproximateDateTime(timeStr);

                            ArticleDto dto = ArticleDto.builder()
                                    .articleCid(cid)
                                    .title(title)
                                    .summary(summary)
                                    .category(categoryName)
                                    .source("연합뉴스")
                                    .articleUrl(fullUrl)
                                    .thumbnailUrl(thumbnailUrl)
                                    .publishedAt(publishedAt)
                                    .build();
                            articleDtoList.add(dto);

                            log.debug("Parsed article: {}", dto);
                        } else {
                            log.warn("필드가 없을 경우 (title, summary, time). CID: {}", cid);
                        }
                    } catch (Exception e) {
                        // 특정 기사 파싱 중 에러 발생 시 로그 남기고 계속 진행
                        log.error("파싱 실패 : {}", articleElement.html(), e);
                    }
                }
            }
            log.info("파싱 성공 개수 {} 카테고리 : {}", articleDtoList.size(), categoryName);
            return articleDtoList;
        });
    }

    // 목록 페이지 시간 문자열 파싱
    private LocalDateTime parseApproximateDateTime(String timeStr) {
        try {
            // 현재 연도 가져오기
            int currentYear = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")).getYear();
            String yearAndTimeStr = currentYear + "-" + timeStr; // 예: "2025-10-26 17:18"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(yearAndTimeStr, formatter);
        } catch (Exception e) {
            log.warn("시간 변환 실패: {}", timeStr, e);
            return null;
        }
    }
}
