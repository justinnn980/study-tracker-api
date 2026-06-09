package com.jobprep.studytracker.auth.dto;

import com.jobprep.studytracker.user.domain.User;

public record LoginResponse(
        Long id,
        String email,
        String name
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}