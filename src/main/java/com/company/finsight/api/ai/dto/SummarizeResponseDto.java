package com.company.finsight.api.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummarizeResponseDto {
	private String summarize;

	public static SummarizeResponseDto toEntity(String summary) {
		return new SummarizeResponseDto(summary);
	}
}
