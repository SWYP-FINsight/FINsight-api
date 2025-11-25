package com.company.finsight.global.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.company.finsight.api.ai.entity.AIArticle;

@Component
public class AiArticleCache {
	private static final int MAX_CACHE_SIZE = 1000;

	private final Map<Long, AIArticle> cache = new LinkedHashMap<>(16, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, AIArticle> eldest) {
			return size() > MAX_CACHE_SIZE;
		}
	};

	public synchronized void put(Long id, AIArticle article) {
		cache.put(id, article);
	}

	public synchronized Optional<AIArticle> findArticleById(Long id) {
		return Optional.ofNullable(cache.get(id));
	}
}
