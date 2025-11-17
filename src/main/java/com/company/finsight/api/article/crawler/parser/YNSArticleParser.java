package com.company.finsight.api.article.crawler.parser;

import com.company.finsight.api.article.crawler.constant.ArticleType;
import com.company.finsight.api.article.dto.ArticleContentDto;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
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

            // 1. 본문 영역 선택
            Element articleBodyElement = doc.selectFirst("article#articleWrap div.story-news.article");
            String content = "";

            if (articleBodyElement != null) {

                // 2. 불필요한 광고, 스크립트 등 제거 (figure는 남겨둠)
                articleBodyElement.select("aside, .ads-box, .label-box, script").remove();

                // 3. (서울=연합뉴스) 같은 머릿말 제거 (DOM 조작)
                Element firstP = articleBodyElement.selectFirst("p");
                if (firstP != null) {
                    Node firstChild = firstP.firstChild();
                    if (firstChild instanceof TextNode) {
                        TextNode firstTextNode = (TextNode) firstChild;
                        String firstText = firstTextNode.getWholeText();

                        // 정규식으로 머릿말 부분만 찾아서 교체
                        String cleanedText = firstText.replaceFirst("^\\s*\\([^)]+\\)\\s*", "");

                        if (!firstText.equals(cleanedText)) {
                            firstTextNode.text(cleanedText.trim()); // 텍스트 노드 내용 교체
                        }
                    }
                }

                // 4. 기자 이메일, 저작권 문구, 제보 문구가 포함된 <p> 태그 자체를 제거
                //    (이 방식이 HTML 문자열을 직접 자르는 것보다 훨씬 안전합니다.)
                Elements paragraphs = articleBodyElement.select("p");
                for (Element p : paragraphs) {
                    String pText = p.text(); // 태그 제외 순수 텍스트로 검사

                    // (1) 이메일 주소 포함 p 태그 제거 (예: writer@yna.co.kr)
                    if (pText.contains("@yna.co.kr")) {
                        p.remove();
                        continue; // 이미 제거했으므로 다음 태그로
                    }

                    // (2) 제보 문구 포함 p 태그 제거
                    if (pText.contains("제보는 카카오톡 okjebo")) {
                        p.remove();
                        continue;
                    }

                    // (3) 저작권 문구 포함 p 태그 제거
                    if (pText.contains("<저작권자(c) 연합뉴스") || pText.contains("무단 전재-재배포")) {
                        p.remove();
                    }
                }

                // 5. 모든 정리가 끝난 DOM에서 HTML 추출
                content = articleBodyElement.html();

                // 6. (선택적) HTML 추출 후 빈 <p> 태그 정리
                content = content.replaceAll("(?i)<p>\\s*(&nbsp;)?\\s*</p>", "");
                content = content.replaceAll("(?i)<p>\\s*<br\\s*/?>\\s*</p>", "");


            } else {
                log.warn("Article body element not found.");
            }

            // --- 기자명 추출 (기존 코드와 동일) ---
            Element reporterElement = doc.selectFirst("div.writer-zone01 strong.tit-name > a");
            String reporter = "";
            if (reporterElement != null) {
                reporter = reporterElement.text();
            } else {
                log.info("작성자 파악 불가");
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
