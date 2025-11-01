package com.company.finsight.global.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final String message;

	private final T data;

	private final LocalDateTime timestamp;

	private ApiResponse(String message, T data) {
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus code, String message) {
		return ResponseEntity.status(code).body(new ApiResponse<>(message, null));
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus code, String message, T data) {
		return ResponseEntity.status(code).body(new ApiResponse<>(message, data));
	}

	public static <T> ResponseEntity<ApiResponse<PageInfo<T>>> success(HttpStatus code, String message, Page<T> page) {
		return ResponseEntity.status(code).body(new ApiResponse<>(message, new PageInfo<>(page)));
	}

	public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus code, String message) {
		return ResponseEntity.status(code).body(new ApiResponse<>(message, null));
	}

	@Getter
	public static class PageInfo<T> {
		private final List<T> content;
		private final int page;
		private final int size;
		private final int totalPage;
		private final long totalElements;

		public PageInfo(Page<T> page) {
			this.content = page.getContent();
			this.page = page.getNumber();
			this.size = page.getSize();
			this.totalPage = page.getTotalPages();
			this.totalElements = page.getTotalElements();
		}
	}
}
