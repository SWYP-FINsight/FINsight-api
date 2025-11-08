package com.company.finsight.api.ai.dto;

import java.util.List;

import lombok.Data;

@Data
public class SummarizeRequest {
	List<Long> articleIds;
}
