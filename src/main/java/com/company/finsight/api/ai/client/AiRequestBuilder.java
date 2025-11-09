package com.company.finsight.api.ai.client;

import com.company.finsight.api.ai.constant.AiConstants;

public class AiRequestBuilder {
	public static String buildRequestBody(String mergedArticles) {
		return String.format("""
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
            """, escapeJson(AiConstants.SYSTEM_PROMPT), escapeJson(mergedArticles));
	}

	private static String escapeJson(String text) {
		return text
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r");
	}
}
