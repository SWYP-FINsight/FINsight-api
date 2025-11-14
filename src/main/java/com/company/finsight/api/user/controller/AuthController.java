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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입을 수행합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "회원가입이 완료되었습니다.",
            authService.signup(signupRequest)
        );
    }

    @Operation(summary = "로그인", description = "로그인을 수행합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        authService.login(loginRequest);
        return ApiResponse.success(
            HttpStatus.OK,
            "로그인이 완료되었습니다."
        );
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "아이디 중복 확인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<CheckUsernameResponse>> checkUsername(
        @Parameter(description = "확인할 아이디", required = true, example = "testuser") @RequestParam String username
    ) {
        CheckUsernameResponse checkUsernameResponse = authService.checkUsername(username);

        String message = checkUsernameResponse.isAvailable() ? "사용 가능한 아이디입니다." : "이미 사용중인 아이디입니다.";

        return ApiResponse.success(
            HttpStatus.OK,
            message,
            checkUsernameResponse
        );
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인 상태를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 상태 확인 성공")
    })
    @GetMapping("/user/me")
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
