package com.company.finsight.api.article.controller;

import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 기사 목록 조회 (필터링 옵션 포함)
     *
     * @param cursor 커서 (마지막 조회 기사 publishedAt, null이면 처음부터)
     * @param size 조회할 기사 개수 (기본값: 10)
     * @param search 검색어 필터 (선택, 포함 검색)
     * @param period 기간 필터 (선택, yyyy-MM-dd 형식)
     * @param source 출처 필터 (선택)
     * @return 커서 페이징된 기사 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime>>> findList(
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) LocalDate period,
        @RequestParam(required = false) String source
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "기사 목록 조회 성공",
            articleService.findList(cursor, size, search, period, source)
        );
    }

    /**
     * 기사 상세 조회 (본문 포함)
     *
     * @param id 기사 ID
     * @return 기사 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> findById(@PathVariable Long id) {
        ArticleDetailDto article = articleService.findById(id);
        return ApiResponse.success(HttpStatus.OK, "기사 상세 조회 성공", article);
    }
}
