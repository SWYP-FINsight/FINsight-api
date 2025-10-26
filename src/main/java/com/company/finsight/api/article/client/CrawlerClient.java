package com.company.finsight.api.article.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CrawlerClient {

    private final WebClient webClient;

    // 연합뉴스 Base URL
    public CrawlerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://www.yna.co.kr/industry/")
                .build();
    }

    public Mono<String> callIndustEnter() {
        return webClient.get()
                .uri("/industrial-enterprise")
                .retrieve()
                .bodyToMono(String.class);
    }
}
