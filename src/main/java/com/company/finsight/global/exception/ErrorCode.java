package com.company.finsight.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String getMessage();
	HttpStatus getStatus();
}
