package com.company.finsight.api.collection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.collection.dto.CollectionCreateRequest;
import com.company.finsight.api.collection.dto.CollectionResponse;
import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.repository.CollectionRepository;
import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.service.UserService;
import com.company.finsight.global.exception.business.collection.CollectionErrorCode;
import com.company.finsight.global.exception.business.collection.CollectionException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CollectionService {

    private final UserService userService;
    private final CollectionRepository collectionRepository;

    @Transactional
    public CollectionResponse create(Long userId, CollectionCreateRequest request) {
        User user = userService.getUser(userId);

        Collection collection = Collection.create(
            request.getCollectionName(),
            request.getKeyword(),
            request.getPeriodType(),
            request.getSource(),
            user
        );

        Collection savedCollection = collectionRepository.save(collection);

        return CollectionResponse.from(savedCollection);
    }

    @Transactional(readOnly = true)
    public CollectionResponse getCollection(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new CollectionException(CollectionErrorCode.COLLECTION_NOT_FOUND));

        return CollectionResponse.from(collection);
    }
}
