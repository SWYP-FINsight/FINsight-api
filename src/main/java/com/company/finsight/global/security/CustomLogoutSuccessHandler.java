package com.company.finsight.global.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        // Map으로 응답 구조 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃이 완료되었습니다.");
        responseBody.put("timestamp", LocalDateTime.now());

        // JSON으로 변환
        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }
}
