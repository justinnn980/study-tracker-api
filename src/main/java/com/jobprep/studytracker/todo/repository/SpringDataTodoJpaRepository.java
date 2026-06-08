package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTodoJpaRepository extends JpaRepository<Todo, Long> {
}
