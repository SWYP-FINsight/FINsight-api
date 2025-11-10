package com.company.finsight.api.ai.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Component
public class AIClient {

	private final WebClient webClient;

	public AIClient(
		WebClient.Builder builder,
		@Value("${clova.api.url}") String apiUrl,
		@Value("${clova.api.key}") String apiKey,
		@Value("${clova.api.request-id}") String requestId
	) {
		this.webClient = builder
			.baseUrl(apiUrl)
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
			.defaultHeader("Accept", "text/event-stream")
			.defaultHeader("Content-Type", "application/json")
			.build();
	}

	public String summarize(List<String> articles) {
		String merged = String.join("\n\n", articles);
		String requestBody = AiRequestBuilder.buildRequestBody(merged);

		final StringBuilder resultBuilder = new StringBuilder();

		Flux<String> stream = webClient.post()
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(requestBody)
			.retrieve()
			.bodyToFlux(String.class);

		stream.toStream().forEach(chunk -> {
			if (chunk.contains("\"event\":\"result\"") || chunk.contains("\"finishReason\":\"stop\"")) return;

			String content = AiResponseParser.extractMessageContent(chunk);

			if (!content.isEmpty()) resultBuilder.append(content);
		});

		return resultBuilder.toString().trim();
	}
}