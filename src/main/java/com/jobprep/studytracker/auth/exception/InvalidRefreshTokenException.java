package com.jobprep.studytracker.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh Token이 올바르지 않습니다.");
    }
}
