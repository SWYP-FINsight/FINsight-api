package com.company.finsight.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configure(http)) // CORS 활성화 (WebConfig의 설정 사용)
            .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // security 로그인 비활성화(커스텀 로그인 API 사용 예정)
            .httpBasic(AbstractHttpConfigurer::disable) // http basic 비활성화

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요 시 세션 생성: 로그인 성공 시 세션 생성
                .sessionFixation().changeSessionId() // 세션 고정 공격 방어 설정: 로그인 성공 후 세션 ID를 변경함
                .maximumSessions(1) // 동시 로그인 1개만 허용
                .maxSessionsPreventsLogin(false) // 새 로그인 시 기존 세션 만료: 컴퓨터에서 로그인하면 모바일에서 로그아웃됨
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/finsight/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
