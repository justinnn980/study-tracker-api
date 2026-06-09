package com.jobprep.studytracker.auth.dto;

import com.jobprep.studytracker.user.domain.User;

public record LoginResponse(
        Long id,
        String email,
        String name,
        String accessToken
) {
    public static LoginResponse of(User user, String accessToken) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                accessToken
        );
    }
}