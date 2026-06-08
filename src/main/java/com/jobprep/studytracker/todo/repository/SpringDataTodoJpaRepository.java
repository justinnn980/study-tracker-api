package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTodoJpaRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTitleContainingIgnoreCaseOrderByIdAsc(String keyword);

    List<Todo> findByCompletedOrderByIdAsc(boolean completed);

    List<Todo> findByTitleContainingIgnoreCaseAndCompletedOrderByIdAsc(String keyword, boolean completed);
}
