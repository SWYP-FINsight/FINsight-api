package com.company.finsight.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckUsernameResponse {
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
