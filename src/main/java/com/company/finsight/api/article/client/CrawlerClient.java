package com.company.finsight.api.article.client;

import lombok.Getter;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CrawlerClient {

    private final WebClient webClient;

    @Getter
    private final String ynsBaseUrl = "https://www.yna.co.kr";

    // 연합뉴스 Base URL
    public CrawlerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(ynsBaseUrl)
                .build();
    }

    public Mono<String> call(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
