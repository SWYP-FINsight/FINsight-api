package com.company.finsight.api.article.crawler.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CrawlerClient {

    private final WebClient webClient;

    public CrawlerClient(WebClient.Builder webClientBuilder) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
        this.webClient = webClientBuilder
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    public Mono<String> call(String baseUrl, String uri) {
        URI fullUri = URI.create(baseUrl).resolve(uri);

        return webClient.get()
                .uri(fullUri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
