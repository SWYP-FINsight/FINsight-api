package com.company.finsight.api.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.ai.dto.SummarizeRequest;
import com.company.finsight.api.ai.service.AiService;

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
	public ResponseEntity<String> summarize(@RequestBody SummarizeRequest request){
		return ResponseEntity.ok(aiService.summarize(request.getArticleIds()));
	}


}
