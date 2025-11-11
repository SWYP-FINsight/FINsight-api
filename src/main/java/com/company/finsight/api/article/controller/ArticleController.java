package com.company.finsight.api.article.controller;

import com.company.finsight.api.article.dto.ArticleDetailDto;
import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Tag(name = "기사", description = "기사 관련 API")
@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "기사 목록 조회", description = "다양한 필터링 옵션을 사용하여 기사 목록을 커서 기반 페이징으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기사 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime>>> findList(
        @Parameter(description = "다음 페이지를 위한 커서 (마지막으로 조회된 기사의 publishedAt 값)", example = "2024-07-29T10:00:00")
        @RequestParam(required = false) LocalDateTime cursor,

        @Parameter(description = "한 페이지에 보여줄 기사 수", example = "10")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "검색어 (기사 내용에 포함된 단어)", example = "금리")
        @RequestParam(required = false) String search,

        @Parameter(description = "조회 기간 (특정 날짜)", example = "2024-07-29")
        @RequestParam(required = false) LocalDate period,

        @Parameter(description = "기사 출처", example = "연합뉴스")
        @RequestParam(required = false) String source
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "기사 목록 조회 성공",
            articleService.findList(cursor, size, search, period, source)
        );
    }

    @Operation(summary = "기사 상세 조회", description = "특정 기사 ID를 사용하여 기사의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기사 상세 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 기사")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> findById(
        @Parameter(description = "조회할 기사의 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        ArticleDetailDto article = articleService.findById(id);
        return ApiResponse.success(HttpStatus.OK, "기사 상세 조회 성공", article);
    }
}
