package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(String title);

    List<Todo> findAll();

    Optional<Todo> findById(Long id);

    void deleteById(Long id);
}
