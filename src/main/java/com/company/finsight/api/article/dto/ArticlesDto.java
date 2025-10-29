package com.company.finsight.api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticlesDto {
    private Long  id;
    private String title;
    private String subject;
    private String distributor;
    private LocalDateTime timestamp;
}
