package com.jobprep.studytracker.global.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException() {
        super("이미 처리 중이거나 처리된 요청입니다.");
    }
}
