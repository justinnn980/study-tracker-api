package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(String title, User user);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    void delete(Todo todo);

    List<Todo> findAll(Long userId, String keyword, Boolean completed);
}
