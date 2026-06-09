package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTodoRepository implements TodoRepository {
    private final SpringDataTodoJpaRepository jpaRepository;

    public JpaTodoRepository(SpringDataTodoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Todo save(String title, User user) {
        return jpaRepository.save(new Todo(title, user));
    }

    @Override
    public Optional<Todo> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public void delete(Todo todo) {
        jpaRepository.delete(todo);
    }

    @Override
    public List<Todo> findAll(Long userId, String keyword, Boolean completed) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasKeyword && completed != null) {
            return jpaRepository.findByUserIdAndTitleContainingIgnoreCaseAndCompletedOrderByIdAsc(
                    userId,
                    keyword,
                    completed
            );
        }

        if (hasKeyword) {
            return jpaRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByIdAsc(userId, keyword);
        }

        if (completed != null) {
            return jpaRepository.findByUserIdAndCompletedOrderByIdAsc(userId, completed);
        }

        return jpaRepository.findByUserIdOrderByIdAsc(userId);
    }

    @Override
    public long countByUserId(Long userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public long countByUserIdAndCompleted(Long userId, boolean completed) {
        return jpaRepository.countByUserIdAndCompleted(userId, completed);
    }
}
