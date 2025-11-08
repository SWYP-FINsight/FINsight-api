package com.company.finsight.api.ai.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.company.finsight.global.exception.business.ai.AiErrorCode;
import com.company.finsight.global.exception.business.ai.AiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Component
public class AIClient {

	private final WebClient webClient;

	private final String SYSTEM_PROMPT = """
        [역할] 
        당신은 여러 산업 분야(예: 제조, 건설, 해양, 자동차, 에너지, 금융 등)의 기사 본문을 입력받아, 
        서로 다른 사례를 하나의 시장 흐름으로 융합해 통합 요약문을 작성하는 산업 분석 요약기이다. 
        입력에는 기업명·지역명·기관명·인물명 등이 포함되더라도, 출력에서는 절대 언급하지 않는다. 
        
        [출력 형식] 
        - 3~4문장, 250~400자 내로 작성 (절대 500자 초과 금지)
        - 단락은 1개만 사용하며, 줄바꿈은 최대 2회까지만 허용한다.
        - 불릿, 번호, 제목, 리스트, 이모지 금지.
        - 마지막 줄에는 반드시 “AI 총점: ±숫자”만 단독 표기한다. (예: AI 총점: +2)

        [핵심 요약 규칙] 
        1. 모든 고유명사(기업, 기관, 인물, 지역, 브랜드 등)와 구체적 사건명은 전부 삭제한다.
           탐지된 고유명사는 “산업”, “시장”, “분야”, “경제”, “체계”, “환경” 등 추상 명사로 자동 대체한다.
        2. 서로 다른 산업 내용을 하나의 서사로 융합하여 서술하고, 절대 병렬 나열하지 않는다.
        3. 문장은 반드시 “① 변화(무엇이 달라지고 있는가) → ② 영향(그 변화가 산업에 미친 구조적 결과) → ③ 시사점(산업의 방향성 또는 전망)” 구조를 따른다.
        4. 수치, 인용, 발언, 제품명, 정책명 등 세부 요소는 모두 제거한다.
        5. 어조는 객관적이고 분석적이며, 감정이나 예측 표현을 사용하지 않는다.
        6. 출력은 항상 산업 전체의 추세와 구조 변화 중심으로 작성한다.
        7. 여러 산업이 입력되더라도 반드시 하나의 통합된 산업 흐름으로 3~4문장만 작성한다.
        
        [AI 총점 산출 규칙 — 현실 평가형]
        AI는 단순히 어조나 문장 톤이 긍정적이라는 이유로 높은 점수를 주지 않는다.
        산업의 실질적 성장 가능성, 구조 안정성, 기술 경쟁력, 시장 확장성 등을 종합 판단해 점수를 산출한다.
        
        - +5~+3 : 산업 전반의 뚜렷한 성장세, 구조 개선, 기술 혁신이 시장 전반으로 확산되는 매우 긍정적 상황
        - +2~+1 : 일부 긍정 요인이 있으나 산업 전체로는 제한적 회복 또는 안정세 수준
        - 0 : 산업이 조정·균형 국면에 있으며, 성장도 위축도 없는 중립적 상황
        - -1~-2 : 성장세 둔화, 투자 위축, 불확실성 확대 등 약한 부정 국면
        - -3~-5 : 구조적 침체, 수요 급감, 시장 위기, 산업 기반 약화 등 심각한 부정 국면

        [출력 예시 1 — 긍정적 국면 (+4)] 
        산업 전반이 기술 혁신과 글로벌 협력을 중심으로 구조적 변화를 겪고 있다.
        생산성 향상과 친환경 전환이 병행되며, 효율성과 지속 가능성이 동시에 강화되고 있다.
        이는 경기 회복의 동력으로 작용하며 산업 생태계의 안정적 성장을 뒷받침하고 있다.
        AI 총점: +4

        [출력 예시 2 — 중립적 국면 (0)] 
        산업 전반이 구조 조정과 수요 변동의 혼재 속에서 방향성을 모색하고 있다.
        기술 혁신은 이어지고 있으나 투자 위축과 시장 불확실성이 맞물리며 성장세가 제한되고 있다.
        전반적으로 산업은 안정과 조정의 균형을 유지하는 중립적 국면에 머물러 있다.
        AI 총점: 0

        [출력 예시 3 — 부정적 국면 (-3)] 
        산업 전반이 경기 둔화와 수요 위축의 영향을 받으며 성장세가 약화되고 있다.
        투자 심리 위축과 글로벌 공급망 불안이 맞물리며 생산 효율성도 하락하고 있다.
        이는 산업 구조 전반의 불확실성을 확대시키는 부정적 흐름으로 평가된다.
        AI 총점: -3
        """;

	public AIClient(
		WebClient.Builder builder,
		@Value("${clova.api.url}") String apiUrl,
		@Value("${clova.api.key}") String apiKey,
		@Value("${clova.api.request-id}") String requestId) {

		webClient = builder
			.baseUrl(apiUrl)
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
			.defaultHeader("Accept", "text/event-stream")
			.defaultHeader("Content-Type", "application/json")
			.build();
	}

	public String summarize(List<String> contents) {
		StringBuilder merged = new StringBuilder();
		for (String article : contents) {
			merged.append(article).append("\n\n");
		}

		final String requestBody = String.format("""
        {
          "messages": [
            {
              "role": "system",
              "content": "%s"
            },
            {
              "role": "user",
              "content": "%s"
            }
          ],
          "topP": 0.8,
          "temperature": 0.5,
          "maxTokens": 512,
          "repetitionPenalty": 1.1
        }
        """, escapeJson(SYSTEM_PROMPT), escapeJson(merged.toString()));

		final StringBuilder lastChunk = new StringBuilder();

		Flux<String> stream = webClient.post()
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(requestBody)
			.retrieve()
			.bodyToFlux(String.class);

		stream.toStream().forEach(chunk -> {
			if (chunk.contains("\"event\":\"result\"")) return;

			String content = parseResultContent(chunk);
			if (!content.isEmpty()) {
				if (!content.contentEquals(lastChunk)) {
					lastChunk.setLength(0);
					lastChunk.append(content);
				}
			}
		});

		return lastChunk.toString().trim();
	}

	private String escapeJson(String text) {
		return text
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r");
	}
	private String parseResultContent(String chunk) {
		try {
			JsonNode root = new ObjectMapper().readTree(chunk);
			JsonNode message = root.path("message");
			if (message.has("content")) {
				return message.get("content").asText("");
			}
		} catch (Exception e) {
			throw new AiException(AiErrorCode.JSON_PARSE_ERROR);
		}
		return "";
	}
}