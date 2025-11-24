# 핀사이트(Finsight)
## 개요

해당 프로젝트는 핀사이트(Finsight) 서비스의 백엔드 시스템을 구축한 개인/팀 프로젝트로, 안정적인 API 제공, 유연한 확장성, 운영 편의성을 목표로 설계되었습니다.

---
## 주요 기능 (임시)

- 기사 수집·크롤링 파이프라인
- AI 요약 처리(Clova Studio 연동)
- 기사 통합 조회 및 정렬·필터링
- 기사 묶음 요약(멀티 기사 요약)
- 사용자 즐겨찾기 및 조회 히스토리
- 세션 기반 인증(로그인/로그아웃)

---
## 기술스택
```yaml
Backend
    Java 17
    Spring Boot
    Spring Data JPA
    Spring Session
```

```yaml
DB
    PostgreSQL
```

```yaml
Infra & DevOps
    Docker / Docker Compose
    NCP ( Naver Cloud Platform )
```

```yaml
AI / Crawling
    Clova Studio API
```
```yaml
Tooling
  GitHub / GitHub Projects
  IntelliJ
  Postman
```
---
## DB/ERD 구조
( 구조 그림 )

---
## 시스템 아키텍쳐
( 그림 )

---
## 주요 REST API 설계
( 링크 )

## 배포 구조
- 배포 파이프라인
- 서버 구성
- 환경 분리(dev/prod)

## 테스트 전략
- 단위 테스트(Junit)
- 크롤링 모듈 테스트 전략
- 요약 API 검증 방식
- 데이터 정합성 테스트

## 트러블슈팅

## 성능 개선

## 프로젝트에서 담당한 역할
