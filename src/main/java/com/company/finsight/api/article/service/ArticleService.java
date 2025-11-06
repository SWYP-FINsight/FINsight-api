package com.company.finsight.api.article.service;

import com.company.finsight.api.article.client.CrawlerClient;
import com.company.finsight.api.article.domain.Article;
import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.global.ArticleCategory;
import com.company.finsight.global.exception.business.article.ArticleErrorCode;
import com.company.finsight.global.exception.business.article.ArticleException;
import com.company.finsight.api.article.repository.ArticleRepository;
import com.company.finsight.global.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final CrawlerClient crawlerClient;
    private final ArticleParserFactory articleParserFactory;
    private final ArticleRepository articleRepository;

    /**
     * 기사 목록 조회 (필터링 옵션 포함)
     *
     * @param pageable 페이지 정보 (페이지 번호, 크기, 정렬)
     * @param category 카테고리 필터 (선택)
     * @param keyword 키워드 필터 (선택)
     * @param period 기간 필터 (선택)
     * @param source 출처 필터 (선택)
     * @return 페이지네이션된 기사 목록
     */
    public Page<ArticlesDto> findList(Pageable pageable, String category, String keyword, LocalDate period, String source) {
        Page<Article> articlePage;

        if (category != null || keyword != null || period != null || source != null) {
            articlePage = articleRepository.findByFilter(pageable, category, keyword, period, source);
        } else {
            articlePage = articleRepository.findAll(pageable);
        }

        // Article 엔티티를 ArticlesDto로 변환
        return articlePage.map(article -> new ArticlesDto(
                article.getId(),
                article.getTitle(),
                article.getSummary(),
                article.getSource(),
                article.getPublishedAt()
        ));
    }

    /**
     * 기사 상세 조회 (본문 포함)
     *
     * @param id 기사 ID
     * @return 기사 상세 정보
     * @throws ArticleException 기사를 찾을 수 없는 경우
     */
    public ArticleDetailDto findById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        return new ArticleDetailDto(
                article.getId(),
                article.getTitle(),
                article.getSummary(),          
                article.getSource(),           
                article.getPublishedAt(),      
                article.getContent(),
                article.getReporter(),
                article.getArticleUrl(),       
                null,                          // TODO importance (향후 구현 예정)
                article.getKeyword()           
        );
    }

    @Scheduled(fixedDelay = 900000)
    public void scheduledCrawlYNS() {
        log.info("스케줄링 시작...");

        int MAX_CONCURRENCY = 5;
        long startTime = System.currentTimeMillis();

        Flux.fromArray(ArticleCategory.values())
                .flatMap(category ->
                                fetchCategoryArticleList(category)
                                        .flatMap(articleList -> fetchArticleContents(articleList, category))
                                        .doOnError(error -> log.error("크롤링 실패 카테고리 : {}", category.getKoreanName(), error))
                                        .onErrorResume(e -> Mono.empty())
                        , MAX_CONCURRENCY)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("모든 카테고리 크롤링 완료. (총 {}ms 소요)", duration);
                })
                .doOnError(error -> log.error("전체 스케줄링 스트림 에러 발생", error))
                .blockLast();

        log.info("스케줄링 종료.");
    }

    private Mono<List<ArticleSummaryDto>> fetchCategoryArticleList(ArticleCategory category) {
        return crawlerClient.call(category.getPath())
                .flatMap(html -> articleParserFactory.getParser(category.getArticleType()).parseArticleList(html, category.getKoreanName(), crawlerClient.getYnsBaseUrl()))
                .doOnNext(articleList ->
                        log.info("파싱 결과 개수 : {}, 카테고리 : {}", articleList.size(), category.getKoreanName())
                );
    }

    private Mono<Void> fetchArticleContents(List<ArticleSummaryDto> articleList, ArticleCategory category) {
        Random random = new Random();
        int min = 5000;
        int max = 15000;

        return Flux.fromIterable(articleList)
                .concatMap(articleSummary ->
                        crawlerClient.call(articleSummary.getArticleUrl())
                                .flatMap(html -> articleParserFactory.getParser(category.getArticleType()).parseArticleContent(html))
                                .publishOn(Schedulers.boundedElastic())
                                .doOnNext(articleContent -> {
                                    log.info("본문 {} : 기자 {}", articleContent.getContent(), articleContent.getReporter());
                                    if (!validRedundancy(articleSummary.getArticleCid())) {
                                        Article article = Article.create(
                                                articleSummary.getArticleCid(), articleSummary.getTitle(), articleSummary.getSummary(),
                                                articleContent.getContent(), articleSummary.getCategory(), articleContent.getReporter(),
                                                articleSummary.getSource(), findKeywords(articleContent.getContent()), articleSummary.getArticleUrl(),
                                                articleSummary.getThumbnailUrl(), articleSummary.getPublishedAt()
                                        );
                                        articleRepository.save(article);
                                        log.info("본문 저장 성공: {}", article.getArticleCid());
                                    } else {
                                        log.info("이미 수집한 데이터");
                                    }
                                })
                                .doOnError(error -> log.error("본문 크롤링 실패 (개별) : {}", articleSummary.getArticleCid(), error))
                                .onErrorResume(error -> Mono.empty())
                                .then(Mono.delay(Duration.ofMillis(random.nextInt(max - min + 1) + min)))
                )
                .doOnComplete(() -> log.info("카테고리 내 '{}'개의 기사 본문 크롤링 작업 완료.", articleList.size()))
                .doOnError(error -> log.error("개별 카테고리 본문 크롤링 스트림 실패", error))
                .then();
    }

    private String findKeywords(String content) {
        Set<String> keywords = new HashSet<>();
        for(String keyword : Const.KEYWORDS) {
            if(content.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        return String.join(", ", keywords);
    }

    private boolean validRedundancy(String articleCid) {
        return articleRepository.existsByArticleCid(articleCid);
    }
}
