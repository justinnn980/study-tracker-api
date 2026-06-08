package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemoryTodoRepository implements TodoRepository {
    private final List<Todo> todos = new ArrayList<>();
    private long sequence = 0L;

    @Override
    public Todo save(String title) {
        Todo todo = new Todo(++sequence, title);
        todos.add(todo);
        return todo;
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(todos);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todos.stream()
                .filter(todo -> todo.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        todos.removeIf(todo -> todo.getId().equals(id));
    }
}
