package com.company.finsight.api.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummarizeRequestDto {
	private List<Long> articleIds;
}
