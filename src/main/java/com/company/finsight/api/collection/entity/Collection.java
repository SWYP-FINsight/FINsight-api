package com.company.finsight.api.collection.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.company.finsight.api.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "collection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long id;

    @Column(name = "collection_name", nullable = false)
    private String collectionName;

    @Column(name = "keyword")
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type")
    private PeriodType periodType;

    @Column(name = "source")
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Collection(String collectionName, String keyword, PeriodType periodType, String source, User user) {
        this.collectionName = collectionName;
        this.keyword = keyword;
        this.periodType = periodType;
        this.source = source;
        this.user = user;
    }

    public static Collection create(String collectionName, String keyword, PeriodType periodType, String source, User user) {
        return new Collection(
            collectionName,
            keyword,
            periodType,
            source,
            user
        );
    }

    public void update(String collectionName, String keyword, PeriodType periodType, String source) {
        this.collectionName = collectionName;
        this.keyword = keyword;
        this.periodType = periodType;
        this.source = source;
    }
}
