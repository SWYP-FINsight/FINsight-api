package com.company.finsight.global.exception.business;

import com.company.finsight.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}