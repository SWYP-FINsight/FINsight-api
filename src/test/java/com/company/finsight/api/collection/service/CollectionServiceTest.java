package com.company.finsight.api.collection.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.finsight.api.article.dto.ArticlesDto;
import com.company.finsight.api.article.service.ArticleService;
import com.company.finsight.api.collection.dto.CollectionInfo;
import com.company.finsight.api.collection.dto.CollectionListResponse;
import com.company.finsight.api.collection.dto.CollectionResponse;
import com.company.finsight.api.collection.entity.Collection;
import com.company.finsight.api.collection.repository.CollectionRepository;
import com.company.finsight.global.response.ApiResponse;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

	@Mock
	private ArticleService articleService;
	@Mock
	private CollectionRepository collectionRepository;

	@InjectMocks
	private CollectionService collectionService;

	@Test
	@DisplayName("컬렉션_조회에_성공한다")
	void getMyCollection() {
		// given
		Collection collection = mock(Collection.class);
		ReflectionTestUtils.setField(collection, "id", 1L);

		when(collectionRepository.findById(anyLong()))
			.thenReturn(Optional.of(collection));

		// when
		CollectionResponse response = collectionService.getMyCollection(1L);

		// then
		assertEquals(collection.getId(), response.id());
	}

	@Test
	@DisplayName("특정_유저의_컬렉션_조회에_성공한다")
	void getMyCollections() {
		//g
		List<CollectionInfo> infos = List.of(mock(CollectionInfo.class));

		when(collectionRepository.findAllByUserId(anyLong())).thenReturn(infos);

		//w
		CollectionListResponse result = collectionService.getMyCollections(1L);

		//t
		assertTrue(result.getCollections().isEmpty());
	}

	@Test
	@DisplayName("컬렉션_기준으로_기사_목록_조회에_성공한다")
	void getArticlesByCollection() {
		// given
		Long collectionId = 1L;
		LocalDateTime cursor = LocalDateTime.now();
		int size = 10;

		Collection collection = mock(Collection.class);
		when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

		when(collection.getKeyword()).thenReturn("테스트키워드");
		when(collection.getSource()).thenReturn("테스트소스");
		when(collection.getPeriodType()).thenReturn(null);

		ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime> expectedResult =
			new ApiResponse.CursorPageInfo<>(List.of(), null, false);

		when(articleService.findList(any(), anyInt(), any(), any(), any()))
			.thenReturn(expectedResult);

		// when
		ApiResponse.CursorPageInfo<ArticlesDto, LocalDateTime> actualResult =
			collectionService.getArticlesByCollection(collectionId, cursor, size);

		// then
		assertEquals(expectedResult, actualResult);
		verify(collectionRepository).findById(collectionId);
		verify(articleService).findList(eq(cursor), eq(size), eq("테스트키워드"), isNull(), eq("테스트소스"));
	}
}