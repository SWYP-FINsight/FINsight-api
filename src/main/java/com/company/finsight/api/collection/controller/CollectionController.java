package com.company.finsight.api.collection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.collection.dto.CollectionCreateRequest;
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
}
