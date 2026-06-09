package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataTodoJpaRepository extends JpaRepository<Todo, Long> {
    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    List<Todo> findByUserIdOrderByIdAsc(Long userId);

    List<Todo> findByUserIdAndTitleContainingIgnoreCaseOrderByIdAsc(Long userId, String keyword);

    List<Todo> findByUserIdAndCompletedOrderByIdAsc(Long userId, boolean completed);

    List<Todo> findByUserIdAndTitleContainingIgnoreCaseAndCompletedOrderByIdAsc(
            Long userId,
            String keyword,
            boolean completed
    );
}
