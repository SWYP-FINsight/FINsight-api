package com.company.finsight.global.exception.business.user;

import org.springframework.http.HttpStatus;

import com.company.finsight.global.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저를 찾을 수 없습니다."),
	ALREADY_EXISTS_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 유저 아이디입니다.");

	private final HttpStatus status;
	private final String message;
}
