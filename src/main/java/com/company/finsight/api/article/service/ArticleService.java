package com.company.finsight.api.article.service;

import com.company.finsight.api.article.crawler.client.CrawlerClient;
import com.company.finsight.api.article.crawler.constant.ArticleCategory;
import com.company.finsight.api.article.crawler.parser.ArticleParserFactory;
import com.company.finsight.api.article.domain.Article;
import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticleSummaryDto;
import com.company.finsight.api.article.dto.ArticlesDto;
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

    /**
     * 기사 ID 리스트로 본문(content) 리스트 조회
     *
     * @param ids 기사 ID 리스트
     * @return 기사 본문 리스트
     */
    public List<String> findContentsByIds(List<Long> ids) {
        return articleRepository.findContentsByIdIn(ids);
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
        return crawlerClient.call(category.getArticleType().getBaseUrl(), category.getPath())
                .flatMap(html -> articleParserFactory.getParser(category.getArticleType()).parseArticleList(html, category.getKoreanName(), category.getArticleType().getBaseUrl()))
                .map(articleList -> filterIncrementalArticles(articleList, category))
                .doOnNext(articleList ->
                        log.info("필터링 후 처리할 기사 개수 : {}, 카테고리 : {}", articleList.size(), category.getKoreanName())
                );
    }

    /**
     * 최근 기사만 필터링
     */
    private List<ArticleSummaryDto> filterIncrementalArticles(List<ArticleSummaryDto> articleList, ArticleCategory category) {
        if (articleList.isEmpty()) {
            return articleList;
        }

        String source = articleList.get(0).getSource();
        
        // DB에서 해당 출처의 가장 최근 기사 조회
        Optional<Article> latestArticle = articleRepository.findLatestBySource(source);
        
        if (latestArticle.isEmpty()) {
            log.info("첫 크롤링 - 모든 기사 수집 ({} 출처)", source);
            return articleList;
        }

        String latestCid = latestArticle.get().getArticleCid();
        java.time.LocalDateTime latestPublishedAt = latestArticle.get().getPublishedAt();
        
        log.info("마지막 수집 기사 - CID: {}, 발행일: {}", latestCid, latestPublishedAt);

        // 마지막 기사 이후의 것만
        List<ArticleSummaryDto> newArticles = new ArrayList<>();
        
        for (ArticleSummaryDto article : articleList) {
            // CID가 같으면 여기서 중단
            if (article.getArticleCid().equals(latestCid)) {
                log.info("마지막 수집 기사 발견 - 증분 크롤링 중단");
                break;
            }
            
            // 날짜가 더 최신이거나 같은 경우만 추가
            if (article.getPublishedAt() != null && 
                (article.getPublishedAt().isAfter(latestPublishedAt) || 
                 article.getPublishedAt().isEqual(latestPublishedAt))) {
                newArticles.add(article);
            }
        }

        // 배치 중복 체크
        if (!newArticles.isEmpty()) {
            List<String> cids = newArticles.stream()
                    .map(ArticleSummaryDto::getArticleCid)
                    .toList();
            
            Set<String> existingCids = new HashSet<>(articleRepository.findExistingCids(cids));
            
            newArticles = newArticles.stream()
                    .filter(article -> !existingCids.contains(article.getArticleCid()))
                    .toList();
            
            log.info("배치 중복 체크 완료 - 실제 신규 기사: {}개", newArticles.size());
        }

        log.info("증분 크롤링 결과: 전체 {} -> 신규 {}개 ({})", 
                articleList.size(), newArticles.size(), source);
        
        return newArticles;
    }

    private Mono<Void> fetchArticleContents(List<ArticleSummaryDto> articleList, ArticleCategory category) {
        if (articleList.isEmpty()) {
            log.info("처리할 신규 기사가 없습니다. 카테고리: {}", category.getKoreanName());
            return Mono.empty();
        }

        Random random = new Random();
        int min = 5000;
        int max = 15000;

        return Flux.fromIterable(articleList)
                .concatMap(articleSummary ->
                        crawlerClient.call(category.getArticleType().getBaseUrl(), articleSummary.getArticleUrl())
                                .flatMap(html -> articleParserFactory.getParser(category.getArticleType()).parseArticleContent(html))
                                .publishOn(Schedulers.boundedElastic())
                                .doOnNext(articleContent -> {
                                    log.info("본문 크롤링 성공 - CID: {}", articleSummary.getArticleCid());
                                    
                                    // 이미 필터링되었으므로 바로 저장
                                    Article article = Article.create(
                                            articleSummary.getArticleCid(), articleSummary.getTitle(), articleSummary.getSummary(),
                                            articleContent.getContent(), articleSummary.getCategory(), articleContent.getReporter(),
                                            articleSummary.getSource(), findKeywords(articleContent.getContent()), articleSummary.getArticleUrl(),
                                            articleSummary.getThumbnailUrl(), articleSummary.getPublishedAt()
                                    );
                                    articleRepository.save(article);
                                    log.info("본문 저장 성공: {}, 기자: {}", article.getArticleCid(), articleContent.getReporter());
                                })
                                .doOnError(error -> log.error("본문 크롤링 실패 (개별) : {}", articleSummary.getArticleCid(), error))
                                .onErrorResume(error -> Mono.empty())
                                .then(Mono.delay(Duration.ofMillis(random.nextInt(max - min + 1) + min)))
                )
                .doOnComplete(() -> log.info("카테고리 '{}'의 {}개 기사 본문 크롤링 작업 완료.", category.getKoreanName(), articleList.size()))
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
}
