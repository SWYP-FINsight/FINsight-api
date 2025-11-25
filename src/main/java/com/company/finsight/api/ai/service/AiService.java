package com.company.finsight.api.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.ai.client.AIClient;
import com.company.finsight.api.ai.dto.SummarizeRequestDto;
import com.company.finsight.api.ai.dto.SummarizeResponseDto;
import com.company.finsight.api.ai.dto.SummarizeSingleRequestDto;
import com.company.finsight.api.ai.entity.AIArticle;
import com.company.finsight.api.ai.repository.AiArticleRepository;
import com.company.finsight.api.article.domain.Article;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.global.cache.AiArticleCache;
import com.company.finsight.global.exception.business.article.ArticleErrorCode;
import com.company.finsight.global.exception.business.article.ArticleException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AiService {

	private final ArticleService articleService;
	private final AIClient aiClient;

	private final AiArticleRepository  aiArticleRepository;
	private final AiArticleCache aiCache;

	@Transactional(readOnly = true)
	public SummarizeResponseDto summarize(SummarizeRequestDto requestDto) {
		List<String> contents = articleService.findContentsByIds(requestDto.getArticleIds());
		String summary = aiClient.summarize(contents);

		return SummarizeResponseDto.toEntity(summary);
	}

	@Transactional
	public SummarizeResponseDto summarize(SummarizeSingleRequestDto requestDto) {

		Long id = requestDto.getArticleId();

		Optional<AIArticle> cacheAiArticleOpt = aiCache.findArticleById(id);
		if ( cacheAiArticleOpt.isPresent() ){
			return SummarizeResponseDto.toEntity(cacheAiArticleOpt.get().getSummary());
		}


		Optional<AIArticle> dbAiArticleOpt =  aiArticleRepository.findById(id);
		if ( dbAiArticleOpt.isPresent() ){
			aiCache.put(id, dbAiArticleOpt.get());
			return SummarizeResponseDto.toEntity(dbAiArticleOpt.get().getSummary());
		}

		List<String> contents = articleService.findContentsByIds(List.of(id));
		Optional<String> contentOpt = contents.stream().findFirst();

		String content = contentOpt.orElseThrow(()->new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));
		String summary = aiClient.summarize(content);

		Article article = articleService.findArticleById(id);
		AIArticle aiArticle = new AIArticle(article, summary);

		aiCache.put(id, aiArticle);
		aiArticleRepository.save(aiArticle);

		return SummarizeResponseDto.toEntity(summary);
	}
}