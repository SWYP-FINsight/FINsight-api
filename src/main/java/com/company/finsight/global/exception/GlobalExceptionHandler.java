package com.company.finsight.global.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.company.finsight.global.exception.business.BusinessException;
import com.company.finsight.global.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 비즈니스 예외 처리
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
		return ApiResponse.error(
			e.getErrorCode().getStatus(),
			e.getMessage()
		);
	}

	/**
	 * @Valid 검증 실패 예외 처리
	 * 회원가입, 로그인 등 DTO 검증 실패 시
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		// 첫 번째 에러 메시지만 반환
		String errorMessage = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getDefaultMessage())
			.findFirst()
			.orElse("입력값이 올바르지 않습니다.");

		return ApiResponse.error(
			HttpStatus.BAD_REQUEST,
			errorMessage
		);
	}

	/**
	 * JSON 파싱 실패 예외 처리
	 * 예) POST /finsight/auth/login
	 * {"username": "admin", "password": } ← 값 누락
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		return ApiResponse.error(
			HttpStatus.BAD_REQUEST,
			"잘못된 요청 형식입니다."
		);
	}

	/**
	 * 예상하지 못한 모든 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		return ApiResponse.error(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"서버 오류가 발생했습니다."
		);
	}

	// ===================== AuthenticationManager 예외 =====================
	/**
	 * 로그인 실패 예외 처리 (비밀번호 틀림)
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
		return ApiResponse.error(
			HttpStatus.UNAUTHORIZED,
			"아이디 또는 비밀번호가 일치하지 않습니다."
		);
	}

	/**
	 * 사용자를 찾을 수 없음 예외 처리
	 * 보안상 BadCredentialsException과 동일한 메시지 반환
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUsernameNotFound(UsernameNotFoundException e) {
		return ApiResponse.error(
			HttpStatus.UNAUTHORIZED,
			"아이디 또는 비밀번호가 일치하지 않습니다."
		);
	}
}
