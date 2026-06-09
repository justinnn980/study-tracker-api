package com.jobprep.studytracker.auth.security;

public record AuthUser(
        Long id,
        String email
) {
}