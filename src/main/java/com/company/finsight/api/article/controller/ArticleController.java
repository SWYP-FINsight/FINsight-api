package com.company.finsight.api.article.controller;

import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticleFilterDto;
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

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 기사 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 페이지네이션된 기사 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.PageInfo<ArticlesDto>>> findList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<ArticlesDto> articlePage = articleService.findList(pageable);
        return ApiResponse.success(HttpStatus.OK, "기사 목록 조회 성공", articlePage);
    }

    /**
     * 기사 목록 필터링 조회
     *
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @param requestDto 필터링 DTO
     * @return 페이지네이션된 기사 목록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ApiResponse.PageInfo<ArticlesDto>>> findList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody ArticleFilterDto requestDto
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<ArticlesDto> articlePage = articleService.findList(pageable, requestDto);
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
