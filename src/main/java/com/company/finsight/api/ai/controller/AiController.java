package com.company.finsight.api.ai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.ai.dto.SummarizeRequestDto;
import com.company.finsight.api.ai.dto.SummarizeResponseDto;
import com.company.finsight.api.ai.dto.SummarizeSingleRequestDto;
import com.company.finsight.api.ai.service.AiService;
import com.company.finsight.global.response.ApiResponse;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AiController {
	private final AiService aiService;
	/**
	 * @param request 기사 번호 List
	 * {
	 *   "articleIds": [1, 2, 3, 4, 5]
	 * }
	 */
	@PostMapping("/ai/summarize")
	public ResponseEntity<ApiResponse<SummarizeResponseDto>> summarize(@RequestBody SummarizeRequestDto request){
		return ApiResponse.success(
				HttpStatus.OK,
				"선택된 기사들이 요약되었습니다.",
				aiService.summarize(request)
		);
	}
	@PostMapping("/ai/summarize/article")
	public ResponseEntity<ApiResponse<SummarizeResponseDto>> summarize(
		@RequestBody SummarizeSingleRequestDto request
	){
		return ApiResponse.success(
			HttpStatus.OK,
			"단일 기사가 요약되었습니다.",
			aiService.summarize(request)
		);
	}
}
