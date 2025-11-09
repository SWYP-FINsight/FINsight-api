package com.company.finsight.api.ai.client;

import com.company.finsight.global.exception.business.ai.AiErrorCode;
import com.company.finsight.global.exception.business.ai.AiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AiResponseParser {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String extractMessageContent(String chunk) {
		try {
			JsonNode root = mapper.readTree(chunk);
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
