package com.company.finsight.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 상태 확인 응답 DTO")
public class CheckLoginStatusResponse {

    @Schema(description = "로그인 여부", example = "true")
    private boolean isLoggedIn;

    @Schema(description = "사용자 아이디", example = "testuser")
    private String username;

    private CheckLoginStatusResponse(boolean isLoggedIn, String username) {
        this.isLoggedIn = isLoggedIn;
        this.username = username;
    }

    public static CheckLoginStatusResponse of(boolean isLoggedIn, String username) {
        return new CheckLoginStatusResponse(
            isLoggedIn,
            username
        );
    }
}
