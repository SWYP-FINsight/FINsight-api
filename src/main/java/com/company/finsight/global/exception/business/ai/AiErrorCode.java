package com.company.finsight.global.exception.business.ai;

import org.springframework.http.HttpStatus;

import com.company.finsight.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {
    AI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "현재 AI 요약이 응답하지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}