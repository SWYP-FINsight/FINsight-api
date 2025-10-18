package com.company.finsight.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.company.finsight.global.exception.business.BusinessException;
import com.company.finsight.global.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handler(BusinessException e){
		return ApiResponse
			.error(
				e.getErrorCode().getStatus(),
				e.getMessage()
			);
	}

	protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
		return ApiResponse
			.error(
				e.getErrorCode().getStatus(),
				e.getMessage()
			);
	}
}
