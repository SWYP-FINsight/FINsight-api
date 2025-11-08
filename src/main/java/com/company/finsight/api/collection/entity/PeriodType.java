package com.company.finsight.api.collection.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodType {
    TODAY("오늘", 0),
    LAST_7_DAYS("7일 이내", 7),
    LAST_30_DAYS("30일 이내", 30);

    private final String description;
    private final int days;

    /**
     * 기간의 시작 시간을 계산
     * @return 시작 LocalDateTime
     */
    public LocalDateTime getStartDateTime() {
        if (this == TODAY) {
            // 오늘 00시 00분 00초
            return LocalDateTime.now().toLocalDate().atStartOfDay();
        }
        // 현재 시간으로부터 N일 전
        return LocalDateTime.now().minusDays(days);
    }
}
