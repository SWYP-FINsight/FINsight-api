package com.company.finsight.api.collection.repository;

import java.util.List;

import com.company.finsight.api.collection.dto.CollectionInfo;

public interface CollectionDslRepository {

    List<CollectionInfo> findAllByUserId(Long userId);
}
