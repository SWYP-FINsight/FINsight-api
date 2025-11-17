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

                // --- DOM을 직접 수정하여 불필요한 부분 제거 ---

                // 1. 본문 끝에 붙어있는 기자 정보 제거 (DOM에서 직접 노드 제거)
                List<Node> childNodes = articleBodyElement.childNodes();
                if (!childNodes.isEmpty()) {
                    // 마지막 노드를 가져옵니다.
                    Node lastNode = childNodes.get(childNodes.size() - 1);

                    // 마지막 노드가 "이름 기자 이메일" 형식의 텍스트 노드인지 확인
                    if (lastNode instanceof TextNode) {
                        String lastText = ((TextNode) lastNode).getWholeText().trim();

                        if (lastText.matches("[가-힣]+\\s+기자\\s+.*@.*")) {
                            // 1. 일치하는 텍스트 노드 제거
                            lastNode.remove();

                            // 2. 텍스트 노드 바로 앞의 <br> 태그도 확인 후 제거
                            if (!articleBodyElement.childNodes().isEmpty()) {
                                Node nodeBefore = articleBodyElement.lastChild();
                                if (nodeBefore != null && nodeBefore.nodeName().equals("br")) {
                                    nodeBefore.remove();
                                }
                            }
                        }
                    }
                }

                // 2. (선택 사항) 본문 시작의 (서울=연합뉴스) 같은 머릿말 제거
                if (!articleBodyElement.childNodes().isEmpty()) {
                    Node firstNode = articleBodyElement.childNode(0);

                    // 첫 번째 노드가 텍스트 노드인 경우
                    if (firstNode instanceof TextNode) {
                        TextNode firstTextNode = (TextNode) firstNode;
                        String firstText = firstTextNode.getWholeText();

                        // 정규식으로 머릿말 부분만 제거
                        String cleanedText = firstText.replaceFirst("^\\s*\\([^)]+\\)\\s*", "");

                        // 텍스트 노드의 내용을 교체
                        if (!firstText.equals(cleanedText)) {
                            firstTextNode.text(cleanedText); // 텍스트 노드 내용 교체
                        }
                    }
                }
                // --- DOM 수정 끝 ---

                // 3. Jsoup DOM에서 불필요한 요소를 제거했으므로,
                //    이제 .html()을 호출하여 태그가 포함된 내용을 추출합니다.
                content = articleBodyElement.html();

                // 4. 저작권 문구 제거 로직은 불필요
                //    (제공된 HTML 기준, #articletxt 외부에 있으므로)

            } else {
                log.warn("Article body element not found.");
            }

            // 기자 이름 (기존 코드와 동일)
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
     * 예: /article/202511103488i -> 202511103488i
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
