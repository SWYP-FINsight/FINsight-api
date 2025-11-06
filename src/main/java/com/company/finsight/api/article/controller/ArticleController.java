package com.company.finsight.api.article.controller;

import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 기사 목록 조회 (필터링 옵션 포함)
     *
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @param category 카테고리 필터 (선택)
     * @param keyword 키워드 필터 (선택)
     * @param period 기간 필터 (선택, yyyy-MM-dd 형식)
     * @param source 출처 필터 (선택)
     * @return 페이지네이션된 기사 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.PageInfo<ArticlesDto>>> findList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate period,
            @RequestParam(required = false) String source
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<ArticlesDto> articlePage = articleService.findList(pageable, category, keyword, period, source);
        return ApiResponse.success(HttpStatus.OK, "기사 목록 조회 성공", articlePage);
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
