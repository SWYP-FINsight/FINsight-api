package com.company.finsight.api.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.ai.client.AIClient;
import com.company.finsight.api.article.service.ArticleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AiService {

	private final ArticleService articleService;
	private final AIClient aiClient;

	@Transactional(readOnly = true)
	public String summarize(List<Long> articleIds){
		return aiClient.summarize(
			articleService.findContentsByIds(articleIds)
		);
	}

}
