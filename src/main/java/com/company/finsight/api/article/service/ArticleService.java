package com.company.finsight.api.article.service;

import com.company.finsight.api.article.client.CrawlerClient;
import com.company.finsight.api.article.domain.Article;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import com.company.finsight.api.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final CrawlerClient crawlerClient;
    private final ArticleParser articleParser;
    private final ArticleRepository articleRepository;

    @Scheduled(fixedRate = 10000)
    public void test() {
        log.info("스케줄링 시작...");
        String category = "산업/기업"; // 현재 크롤링 중인 카테고리명

        crawlerClient.callIndustEnter() // HTML 가져오기 (Mono<String>)
                .flatMap(html -> articleParser.parseArticleList(html, category, crawlerClient.getYnsBaseUrl())) // HTML 파싱하여 DTO 리스트 생성 (Mono<List<ArticleDto>>)
                .doOnNext(articleList -> {
                    log.info("파싱 결과 개수 : {}, 카테고리 : {}", articleList.size(), category);
                    callContent(articleList);
                })
                .doOnError(error -> {
                    log.error("크롤링 실패 카테고리 : {}", category, error);
                })
                .subscribe(); // 실행
        log.info("스케줄링 종료...");
    }

    private void callContent(List<ArticleSummaryDto> articleList) {
        for(ArticleSummaryDto article : articleList) {
            crawlerClient.callContent(article.getArticleUrl())
                    .flatMap(articleParser::parseArticleContent)
                    .doOnNext(articleContent -> {
                       // TODO: 여기서 DB에 저장하는 로직 작성
                        log.info("본문 {} : 기자 {}", articleContent.getContent(), articleContent.getReporter());
                    })
                    .doOnError(error -> {
                        log.error("본문 크롤링 실패 : {}", article.getArticleCid(), error);
                    })
                    .subscribe();
        }
    }


}
