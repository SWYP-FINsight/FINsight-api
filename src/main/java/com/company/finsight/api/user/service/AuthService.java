package com.company.finsight.api.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.finsight.api.user.dto.CheckLoginStatusResponse;
import com.company.finsight.api.user.dto.CheckUsernameResponse;
import com.company.finsight.api.user.dto.LoginRequest;
import com.company.finsight.api.user.dto.SignupRequest;
import com.company.finsight.api.user.dto.SignupResponse;
import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.repository.UserRepository;
import com.company.finsight.global.exception.business.user.UserErrorCode;
import com.company.finsight.global.exception.business.user.UserException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입 로직
     * 비밀번호 암호화
     */
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        // 아이디 중복 검증
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserException(UserErrorCode.ALREADY_EXISTS_USERNAME);
        }

        // 유저 엔티티 생성
        User user = User.create(
            signupRequest.getUsername(),
            passwordEncoder.encode(signupRequest.getPassword()) // 비밀번호 암호화
        );

        // 유저 엔티티 DB 저장
        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }

    public void login(LoginRequest loginRequest) {
        // 1. 인증 처리
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // 2. SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public CheckUsernameResponse checkUsername(String username) {
        return CheckUsernameResponse.from(!userRepository.existsByUsername(username));
    }

    public CheckLoginStatusResponse checkLoginStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return CheckLoginStatusResponse.of(false, null);
        }

        return CheckLoginStatusResponse.of(true, authentication.getName());
    }
}
