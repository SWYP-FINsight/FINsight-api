package com.company.finsight.api.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.ai.client.AIClient;
import com.company.finsight.api.ai.dto.SummarizeRequestDto;
import com.company.finsight.api.ai.dto.SummarizeResponseDto;
import com.company.finsight.api.ai.dto.SummarizeSingleRequestDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.global.exception.business.article.ArticleErrorCode;
import com.company.finsight.global.exception.business.article.ArticleException;

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

	@Transactional(readOnly = true)
	public SummarizeResponseDto summarize(SummarizeSingleRequestDto requestDto) {

		List<String> contents = articleService.findContentsByIds(List.of(requestDto.getArticleId()));
		Optional<String> contentOpt = contents.stream().findFirst();

		String content = contentOpt.orElseThrow(()->new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));
		String summary = aiClient.summarize(content);

		return SummarizeResponseDto.toEntity(summary);
	}
}
