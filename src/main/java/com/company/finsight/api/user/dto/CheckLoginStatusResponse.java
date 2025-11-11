package com.company.finsight.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckLoginStatusResponse {

    private boolean isLoggedIn;
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
