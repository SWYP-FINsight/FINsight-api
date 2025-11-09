package com.company.finsight.api.collection.repository;

import static com.company.finsight.api.collection.entity.QCollection.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.finsight.api.collection.dto.CollectionInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CollectionDslRepositoryImpl implements CollectionDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<CollectionInfo> findAllByUserId(Long userId) {
        return queryFactory
            .select(
                Projections.constructor(
                    CollectionInfo.class,
                    collection.id,
                    collection.collectionName
                )
            )
            .from(collection)
            .where(collection.user.id.eq(userId))
            .orderBy(collection.id.desc())
            .fetch();
    }
}

