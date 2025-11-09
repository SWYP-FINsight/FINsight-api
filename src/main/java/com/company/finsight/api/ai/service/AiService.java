package com.company.finsight.api.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.ai.client.AIClient;
import com.company.finsight.api.ai.dto.SummarizeRequestDto;
import com.company.finsight.api.ai.dto.SummarizeResponseDto;
import com.company.finsight.api.article.service.ArticleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AiService {

	private final ArticleService articleService;
	private final AIClient aiClient;

	@Transactional(readOnly = true)
	public SummarizeResponseDto summarize(SummarizeRequestDto requestDto) {
		List<String> contents = articleService.findContentsByIds(requestDto.getArticleIds());
		String summary = aiClient.summarize(contents);

		return SummarizeResponseDto.toEntity(summary);
	}
}
