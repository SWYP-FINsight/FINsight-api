package com.company.finsight.global.exception.business.collection;

import org.springframework.http.HttpStatus;

import com.company.finsight.global.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectionErrorCode implements ErrorCode {
	COLLECTION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 컬렉션을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String message;
}
