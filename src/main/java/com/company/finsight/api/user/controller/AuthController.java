package com.company.finsight.api.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.finsight.api.user.dto.CheckLoginStatusResponse;
import com.company.finsight.api.user.dto.CheckUsernameResponse;
import com.company.finsight.api.user.dto.LoginRequest;
import com.company.finsight.api.user.dto.SignupRequest;
import com.company.finsight.api.user.dto.SignupResponse;
import com.company.finsight.api.user.service.AuthService;
import com.company.finsight.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "회원가입이 완료되었습니다.",
            authService.signup(signupRequest)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        authService.login(loginRequest);
        return ApiResponse.success(
            HttpStatus.OK,
            "로그인이 완료되었습니다."
        );
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<CheckUsernameResponse>> checkUsername(
        @RequestParam String username
    ) {
        CheckUsernameResponse checkUsernameResponse = authService.checkUsername(username);

        String message = checkUsernameResponse.isAvailable() ? "사용 가능한 아이디입니다." : "이미 사용중인 아이디입니다.";

        return ApiResponse.success(
            HttpStatus.OK,
            message,
            checkUsernameResponse
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CheckLoginStatusResponse>> checkLoginStatus() {
        CheckLoginStatusResponse checkLoginStatusResponse = authService.checkLoginStatus();

        String message = checkLoginStatusResponse.isLoggedIn() ? "로그인 상태입니다." : "로그인 상태가 아닙니다.";

        return ApiResponse.success(
            HttpStatus.OK,
            message,
            checkLoginStatusResponse
        );
    }
}
