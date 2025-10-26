package com.company.finsight.api.user.dto;

import com.company.finsight.api.user.entity.User;

public record SignupResponse(Long userId) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId());
    }
}
