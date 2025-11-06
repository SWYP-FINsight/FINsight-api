package com.company.finsight.api.article.service;

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
public class YNSArticleParser implements ArticleParser {

    public Mono<List<ArticleSummaryDto>> parseArticleList(String htmlContent, String categoryName, String baseUrl) {
        return Mono.fromCallable(() -> {
            List<ArticleSummaryDto> articleDtoList = new ArrayList<>();
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

                            ArticleSummaryDto dto = ArticleSummaryDto.builder()
                                    .articleCid(cid)
                                    .title(title)
                                    .summary(summary)
                                    .category(categoryName)
                                    .source("연합뉴스")
                                    .articleUrl(fullUrl.replace(baseUrl, ""))
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

    public Mono<ArticleContentDto> parseArticleContent(String htmlContent) {
        return Mono.fromCallable(() -> {
            Document doc = Jsoup.parse(htmlContent);
            Element articleBodyElement = doc.selectFirst("article#articleWrap div.story-news.article");
            String content = "";
            if (articleBodyElement != null) {
                content = articleBodyElement.text();

                // 광고 문구 제거
                content = content.replaceFirst("^\\s*\\([^)]+\\)\\s*", "").trim();

                String emailDomain = "@yna.co.kr";
                int emailDomainIndex = content.indexOf(emailDomain); // "@yna.co.kr" 시작 위치 찾기

                if (emailDomainIndex != -1) {
                    int endIndex = emailDomainIndex + emailDomain.length();
                    content = content.substring(0, endIndex).trim();
                } else {
                    // 이메일 주소가 없는 경우
                    int jeboIndex = content.indexOf("제보");
                    if (jeboIndex != -1) {
                        content = content.substring(0, jeboIndex).trim();
                    }
                    // 저작권 문구
                    int copyrightIndex = content.indexOf("<저작권자(c) 연합뉴스");
                    if (copyrightIndex != -1) {
                        content = content.substring(0, copyrightIndex).trim();
                    }
                }

            } else {
                log.warn("Article body element not found.");
            }

            // 작성자 이름 추출
            Element reporterElement = doc.selectFirst("div.writer-zone01 strong.tit-name > a");
            String reporter = "";
            if (reporterElement != null) {
                reporter = reporterElement.text();
            } else {
                // 작성자 정보가 없는 기사
                log.info("작성자 파악 불가");
                // 또는 메타 태그에서 시도
                Element metaAuthor = doc.selectFirst("meta[name=author]");
                if (metaAuthor != null) {
                    reporter = metaAuthor.attr("content");
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
        return ArticleType.YNS;
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
