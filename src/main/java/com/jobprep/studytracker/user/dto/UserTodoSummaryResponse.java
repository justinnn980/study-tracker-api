package com.jobprep.studytracker.user.dto;

import com.jobprep.studytracker.user.domain.User;

public record UserTodoSummaryResponse(
        Long id,
        String email,
        String name,
        int todoCount
) {
    public static UserTodoSummaryResponse from(User user) {
        return new UserTodoSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getTodos().size()
        );
    }
}