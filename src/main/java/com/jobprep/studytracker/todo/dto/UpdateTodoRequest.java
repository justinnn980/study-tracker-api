package com.jobprep.studytracker.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTodoRequest(
        @NotBlank(message = "할 일 제목은 비어 있을 수 없습니다.")
        String title
) {
}
