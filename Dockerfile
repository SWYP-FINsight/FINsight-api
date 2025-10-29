# 1단계: Gradle로 애플리케이션 빌드
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 캐시 최적화를 위해 의존성 먼저 다운로드
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY . .
RUN gradle build --no-daemon -x test

# 2단계: 경량 실행 환경으로 jar 복사
FROM openjdk:17-jre-slim
WORKDIR /app

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-jar", "app.jar"]