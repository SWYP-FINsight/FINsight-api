package com.company.finsight.api.collection.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.collection.dto.CollectionCreateRequest;
import com.company.finsight.api.collection.dto.CollectionListResponse;
import com.company.finsight.api.collection.dto.CollectionResponse;
import com.company.finsight.api.collection.service.CollectionService;
import com.company.finsight.global.response.ApiResponse;
import com.company.finsight.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "컬렉션", description = "사용자가 생성한 기사 컬렉션 관련 API")
@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "새 컬렉션 생성", description = "새로운 기사 컬렉션을 생성합니다. (로그인 필요)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "컬렉션 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionResponse>> create(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CollectionCreateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "컬렉션 등록이 완료되었습니다.",
            collectionService.create(userDetails.getUserId(), request)
        );
    }

    @Operation(summary = "내 컬렉션 상세 조회", description = "특정 컬렉션의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컬렉션 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 컬렉션")
    })
    @GetMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollection(
        @Parameter(description = "조회할 컬렉션의 ID", required = true, example = "1")
        @PathVariable Long collectionId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "컬렉션 조회에 성공하였습니다.",
            collectionService.getMyCollection(collectionId)
        );
    }

    @Operation(summary = "내 모든 컬렉션 목록 조회", description = "로그인한 사용자의 모든 컬렉션 목록을 조회합니다. (로그인 필요)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컬렉션 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<CollectionListResponse>> getMyCollections(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "마이 컬렉션 조회가 완료되었습니다.",
            collectionService.getMyCollections(userDetails.getUserId())
        );
    }

    @Operation(summary = "컬렉션에 속한 기사 목록 조회", description = "특정 컬렉션에 포함된 기사 목록을 커서 기반 페이징으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기사 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 컬렉션")
    })
    @GetMapping("/{collectionId}/articles")
    public ResponseEntity<ApiResponse<ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime>>> getArticlesByCollection(
        @Parameter(description = "조회할 컬렉션의 ID", required = true, example = "1")
        @PathVariable Long collectionId,

        @Parameter(description = "다음 페이지를 위한 커서 (마지막으로 조회된 기사의 publishedAt 값)", example = "2024-07-29T10:00:00")
        @RequestParam(required = false) LocalDateTime cursor,

        @Parameter(description = "한 페이지에 보여줄 기사 수", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "컬렉션을 통한 기사 조회가 완료되었습니다.",
            collectionService.getArticlesByCollection(collectionId, cursor, size)
        );
    }

    @Operation(summary = "내 컬렉션 삭제", description = "특정 컬렉션을 삭제합니다. (로그인 필요)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컬렉션 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 컬렉션")
    })
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteMyCollection(
        @Parameter(description = "삭제할 컬렉션의 ID", required = true, example = "1")
        @PathVariable Long collectionId
    ) {
        collectionService.deleteMyCollection(collectionId);
        return ApiResponse.success(
            HttpStatus.OK,
            "마이 컬렉션 삭제가 완료되었습니다."
        );
    }
}
