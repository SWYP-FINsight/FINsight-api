package com.company.finsight.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아이디 중복 확인 응답 DTO")
public class CheckUsernameResponse {
    @Schema(description = "사용 가능 여부", example = "true")
    private boolean isAvailable;

    private CheckUsernameResponse(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public static CheckUsernameResponse from(boolean isAvailable) {
        return new CheckUsernameResponse(
            isAvailable
        );
    }
}
