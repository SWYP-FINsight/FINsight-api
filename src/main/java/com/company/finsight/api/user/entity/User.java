package com.company.finsight.api.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = true, unique = true)
    private String username; // 로그인용 아이디 (OAuth는 null)

    @Column(nullable = true)
    private String password; // 비밀번호 (OAuth는 null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // 로그인 제공자 (LOCAL, KAKAO)

    @Column(nullable = true, unique = true)
    private String providerId; // OAuth 제공자의 사용자 ID (유니크)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private User(String username, String password, Provider provider, String providerId, Role role) {
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    // 일반 회원가입용 팩터리 메서드
    public static User create(String username, String password) {
        return new User(
            username,
            password,
            Provider.LOCAL,
            null,
            Role.USER);
    }

    // OAuth 로그인용 팩터리 메서드
    public static User createOAuthUser(String username, Provider provider, String providerId) {
        return new User(
            username,
            null, // OAuth는 비밀번호 없음
            provider,
            providerId,
            Role.USER);
    }
}
