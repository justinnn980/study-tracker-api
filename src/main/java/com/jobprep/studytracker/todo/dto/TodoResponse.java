package com.jobprep.studytracker.todo.dto;

import com.jobprep.studytracker.todo.domain.Todo;

public record TodoResponse(Long id, String title, boolean completed) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted());
    }
}