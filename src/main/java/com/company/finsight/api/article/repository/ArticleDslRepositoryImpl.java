package com.company.finsight.api.article.repository;

import com.company.finsight.api.article.domain.Article;
import com.company.finsight.global.Const;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.company.finsight.api.article.domain.QArticle.article;


@Repository
@RequiredArgsConstructor
public class ArticleDslRepositoryImpl implements ArticleDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 필터 조건에 따라 기사를 조회
     *
     * @param pageable 페이징 정보
     * @param keyword 키워드 필터
     * @param period 기간 필터
     * @param source 출처 필터
     * @return 필터링된 기사 목록 (페이징)
     */
    @Override
    public Page<Article> findByFilter(Pageable pageable, String keyword, LocalDate period, String source) {
        // 보관 기간 제한
        LocalDateTime retentionCutoff = LocalDateTime.now().minusDays(Const.ARTICLE_RETENTION_DAYS);
        
        // 쿼리 실행
        List<Article> content = queryFactory
                .selectFrom(article)
                .where(
                        keywordEq(keyword),
                        sourceEq(source),
                        publishedAtAfter(period),
                        article.publishedAt.goe(retentionCutoff) // 보관 기간 제한
                )
                .orderBy(article.publishedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        keywordEq(keyword),
                        sourceEq(source),
                        publishedAtAfter(period),
                        article.publishedAt.goe(retentionCutoff) // 보관 기간 제한
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 키워드 동적 조건
     */
    private BooleanExpression keywordEq(String keyword) {
        return keyword != null ? article.keyword.eq(keyword) : null;
    }

    /**
     * 뉴스 출처 동적 조건
     */
    private BooleanExpression sourceEq(String source) {
        return source != null ? article.source.eq(source) : null;
    }

    /**
     * 발행일 이후 동적 조건
     */
    private BooleanExpression publishedAtAfter(LocalDate period) {
        if (period == null) {
            return null;
        }
        LocalDateTime startOfDay = period.atStartOfDay();
        return article.publishedAt.goe(startOfDay);
    }

    /**
     * ID 리스트로 content만 조회 (프로젝션 쿼리)
     *
     * @param ids 기사 ID 리스트
     * @return 기사 본문 리스트
     */
    @Override
    public List<String> findContentsByIdIn(List<Long> ids) {
        return queryFactory
                .select(article.content)
                .from(article)
                .where(article.id.in(ids))
                .fetch();
    }

    /**
     * 특정 출처의 가장 최근 기사 조회
     */
    @Override
    public Optional<Article> findLatestBySource(String source) {
        Article result = queryFactory
                .selectFrom(article)
                .where(article.source.eq(source))
                .orderBy(article.publishedAt.desc())
                .limit(1)
                .fetchOne();
        
        return Optional.ofNullable(result);
    }

    /**
     * CID 목록으로 이미 존재하는 CID들만 조회 (배치 체크용)
     */
    @Override
    public List<String> findExistingCids(List<String> cids) {
        if (cids == null || cids.isEmpty()) {
            return List.of();
        }
        
        return queryFactory
                .select(article.articleCid)
                .from(article)
                .where(article.articleCid.in(cids))
                .fetch();
    }

    /**
     * 특정 날짜 이전의 기사 개수 조회
     */
    @Override
    public long countByPublishedAtBefore(LocalDateTime beforeDate) {
        Long count = queryFactory
                .select(article.count())
                .from(article)
                .where(article.publishedAt.lt(beforeDate))
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    /**
     * 특정 날짜 이전의 기사 삭제
     */
    @Override
    public int deleteByPublishedAtBefore(LocalDateTime beforeDate) {
        long deletedCount = queryFactory
                .delete(article)
                .where(article.publishedAt.lt(beforeDate))
                .execute();
        
        return (int) deletedCount;
    }
}
