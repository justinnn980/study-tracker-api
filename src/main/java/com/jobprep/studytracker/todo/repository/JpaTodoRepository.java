package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaTodoRepository implements TodoRepository {
    private final SpringDataTodoJpaRepository jpaRepository;

    public JpaTodoRepository(SpringDataTodoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Todo save(String title) {
        return jpaRepository.save(new Todo(title));
    }

    @Override
    public List<Todo> findAll() {
        return jpaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Todo> findAll(String keyword, Boolean completed) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasKeyword && completed != null) {
            return jpaRepository.findByTitleContainingIgnoreCaseAndCompletedOrderByIdAsc(keyword, completed);
        }

        if (hasKeyword) {
            return jpaRepository.findByTitleContainingIgnoreCaseOrderByIdAsc(keyword);
        }

        if (completed != null) {
            return jpaRepository.findByCompletedOrderByIdAsc(completed);
        }

        return findAll();
    }
}
