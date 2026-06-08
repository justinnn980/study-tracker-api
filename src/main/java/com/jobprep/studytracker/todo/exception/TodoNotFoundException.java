package com.jobprep.studytracker.todo.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(Long id) {
        super("Todo를 찾을 수 없습니다. id=" + id);
    }
}