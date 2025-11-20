package com.company.finsight.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final CustomOauth2UserService customOauth2UserService;

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

            .securityContext(context -> context
                .requireExplicitSave(false)) // 기본값: 세션 자동 저장
            // requireExplicitSave(false) = Filter가 자동으로 세션에 저장
            // requireExplicitSave(true) = 수동으로 저장해야 함

            .logout(logout -> logout
                .logoutUrl("/auth/logout") // 로그아웃 url 접근
                .logoutSuccessHandler(logoutSuccessHandler) // 로그아웃 커스텀 핸들러
                .invalidateHttpSession(true) // 세션 무효화
                .clearAuthentication(true) // SecurityContext 클리어
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                .permitAll()
            )

            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOauth2UserService)
                )
                .defaultSuccessUrl("https://localhost:3000/auth/callback", true)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/articles/**").permitAll()
                .requestMatchers("/ai/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/login/oauth2/**").permitAll() // OAuth2 리다이렉트 URL 허용
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
