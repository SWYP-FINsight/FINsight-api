package com.company.finsight.global.exception.business.article;

import com.company.finsight.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArticleErrorCode implements ErrorCode {
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기사를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}