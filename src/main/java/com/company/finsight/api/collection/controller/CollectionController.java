package com.company.finsight.api.collection.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.collection.dto.CollectionCreateRequest;
import com.company.finsight.api.collection.dto.CollectionListResponse;
import com.company.finsight.api.collection.dto.CollectionResponse;
import com.company.finsight.api.collection.service.CollectionService;
import com.company.finsight.global.response.ApiResponse;
import com.company.finsight.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<CollectionResponse>> create(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CollectionCreateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "컬렉션 등록이 완료되었습니다.",
            collectionService.create(userDetails.getUserId(), request)
        );
    }

    /**
     * 마이 컬렉션 상세 조회
     * 추후 추가될 기능
     */
    @GetMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollection(
        @PathVariable Long collectionId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "컬렉션 조회에 성공하였습니다.",
            collectionService.getCollection(collectionId)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CollectionListResponse>> getMyCollections(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "마이 컬렉션 조회가 완료되었습니다.",
            collectionService.getMyCollections(userDetails.getUserId())
        );
    }
}
