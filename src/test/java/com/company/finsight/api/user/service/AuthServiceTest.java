package com.company.finsight.api.user.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.finsight.api.user.dto.CheckLoginStatusResponse;
import com.company.finsight.api.user.dto.CheckUsernameResponse;
import com.company.finsight.api.user.dto.LoginRequest;
import com.company.finsight.api.user.dto.SignupRequest;
import com.company.finsight.api.user.dto.SignupResponse;
import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("회원가입에_성공한다.")
    void signup() {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "username", "testuser");
        ReflectionTestUtils.setField(signupRequest, "password", "password123!");

        String encodedPassword = "encodedPassword123";
        User savedUser = User.create("testuser", encodedPassword);
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        //when
        SignupResponse response = authService.signup(signupRequest);

        //then
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인에_성공한다.")
    void login() {
        //given
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "username", "testuser");
        ReflectionTestUtils.setField(loginRequest, "password", "password123!");

        Authentication authentication = mock(Authentication.class);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);

        //when
        authService.login(loginRequest);

        //then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
    }

    @Test
    @DisplayName("사용_가능한_아이디를_확인한다.")
    void checkUsername() {
        //given
        String username = "testuser";
        given(userRepository.existsByUsername(username)).willReturn(false);

        //when
        CheckUsernameResponse checkUsernameResponse = authService.checkUsername(username);

        //then
        assertThat(checkUsernameResponse.isAvailable()).isTrue();
    }

    @Test
    void checkLoginStatus() {
        //given
        String username = "testuser";
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn(username);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        CheckLoginStatusResponse checkLoginStatusResponse = authService.checkLoginStatus();

        //then
        assertThat(checkLoginStatusResponse.isLoggedIn()).isTrue();
        assertThat(checkLoginStatusResponse.getUsername()).isEqualTo(username);
    }
}