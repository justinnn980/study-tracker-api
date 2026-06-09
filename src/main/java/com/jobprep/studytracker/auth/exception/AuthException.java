package com.jobprep.studytracker.auth.exception;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
