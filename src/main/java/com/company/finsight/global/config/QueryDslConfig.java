package com.company.finsight.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스
 * JPAQueryFactory Bean을 생성하여 QueryDSL을 사용할 수 있도록 설정
 */
@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

    private final EntityManager entityManager;
    
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}