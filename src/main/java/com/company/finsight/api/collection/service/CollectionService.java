package com.company.finsight.api.collection.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.api.collection.dto.CollectionCond;
import com.company.finsight.api.collection.dto.CollectionCreateRequest;
import com.company.finsight.api.collection.dto.CollectionInfo;
import com.company.finsight.api.collection.dto.CollectionListResponse;
import com.company.finsight.api.collection.dto.CollectionResponse;
import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.repository.CollectionRepository;
import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.service.UserService;
import com.company.finsight.global.exception.business.collection.CollectionErrorCode;
import com.company.finsight.global.exception.business.collection.CollectionException;
import com.company.finsight.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CollectionService {

    private final UserService userService;
    private final ArticleService articleService;
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
    public CollectionResponse getMyCollection(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new CollectionException(CollectionErrorCode.COLLECTION_NOT_FOUND));

        return CollectionResponse.from(collection);
    }

    @Transactional(readOnly = true)
    public CollectionListResponse getMyCollections(Long userId) {
        List<CollectionInfo> collectionInfoList = collectionRepository.findAllByUserId(userId);
        return CollectionListResponse.from(collectionInfoList);
    }

    @Transactional(readOnly = true)
    public ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime> getArticlesByCollection(
        Long collectionId,
        LocalDateTime cursor,
        int size
    ) {
        CollectionCond collectionCond = getCollectionCond(collectionId);

        LocalDate period = collectionCond.getPeriodType() != null
            ? collectionCond.getPeriodType().getStartDateTime().toLocalDate()
            : null;

        return articleService.findList(cursor, size,
            collectionCond.getKeyword(), period, collectionCond.getSource());
    }

    @Transactional
    public void deleteMyCollection(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new CollectionException(CollectionErrorCode.COLLECTION_NOT_FOUND));

        collectionRepository.delete(collection);
    }

    private CollectionCond getCollectionCond(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new CollectionException(CollectionErrorCode.COLLECTION_NOT_FOUND));

        return CollectionCond.from(collection);
    }
}
