package com.company.finsight.api.article.service;

import com.company.finsight.api.article.client.CrawlerClient;
import com.company.finsight.api.article.dto.ArticleDto;
import com.company.finsight.api.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                .flatMap(html -> articleParser.parseArticleList(html, category)) // HTML 파싱하여 DTO 리스트 생성 (Mono<List<ArticleDto>>)
                .doOnNext(articleList -> {
                    log.info("파싱 결과 개수 : {}, 카테고리 : {}", articleList.size(), category);
                    // TODO: 여기서 articleList를 반복하며 DB에 저장하는 로직 작성
                    // TODO: 본문 추가 호출 및 파싱 구현 필요
                })
                .doOnError(error -> {
                    log.error("크롤링 실패 카테고리 : {}", category, error);
                })
                .subscribe(); // 실행
        log.info("스케줄링 종료...");
    }


}
