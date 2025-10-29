package com.company.finsight.api.article.service;

import com.company.finsight.api.article.client.CrawlerClient;
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
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final CrawlerClient crawlerClient;
    private final ArticleParser articleParser;
    private final ArticleRepository articleRepository;

    /**
     * 기사 목록 조회 (페이지네이션)
     *
     * @param pageable 페이지 정보 (페이지 번호, 크기, 정렬)
     * @return 페이지네이션된 기사 목록
     */
    public Page<ArticlesDto> findList(Pageable pageable) {
        Page<Article> articlePage = articleRepository.findAll(pageable);

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

    @Scheduled(fixedRate = 900000)
    public void test() {
        log.info("스케줄링 시작...");
        String category = "산업/기업"; // 현재 크롤링 중인 카테고리명

        crawlerClient.callIndustEnter() // HTML 가져오기 (Mono<String>)
                .flatMap(html -> articleParser.parseArticleList(html, category, crawlerClient.getYnsBaseUrl())) // HTML 파싱하여 DTO 리스트 생성 (Mono<List<ArticleDto>>)
                .doOnNext(articleList -> {
                    log.info("파싱 결과 개수 : {}, 카테고리 : {}", articleList.size(), category);
                    callContent(articleList);
                })
                .doOnError(error -> log.error("크롤링 실패 카테고리 : {}", category, error))
                .subscribe(); // 실행
        log.info("스케줄링 종료...");
    }

    private void callContent(List<ArticleSummaryDto> articleList) {
        for(ArticleSummaryDto articleSummary : articleList) {
            crawlerClient.callContent(articleSummary.getArticleUrl())
                    .flatMap(articleParser::parseArticleContent)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext(articleContent -> {
                        log.info("본문 {} : 기자 {}", articleContent.getContent(), articleContent.getReporter());
                        Article article = Article.create(
                            articleSummary.getArticleCid(), articleSummary.getTitle(), articleSummary.getSummary(),
                            articleContent.getContent(), articleSummary.getCategory(), articleContent.getReporter(),
                            articleSummary.getSource(), findKeywords(articleContent.getContent()), articleSummary.getArticleUrl(),
                            articleSummary.getThumbnailUrl(), articleSummary.getPublishedAt()
                        );
                        articleRepository.save(article);
                    })
                    .doOnError(error -> log.error("본문 크롤링 실패 : {}", articleSummary.getArticleCid(), error))
                    .subscribe();

            // 크롤링 대기 시간 설정
            try {
                Random random = new Random();
                int min = 5000;
                int max = 15000;
                int randomNumber = random.nextInt(max - min + 1) + min;
                Thread.sleep(randomNumber);
                log.info("저장 성공");
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
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
