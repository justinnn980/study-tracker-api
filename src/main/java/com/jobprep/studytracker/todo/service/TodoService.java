package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.TodoRepository;

import java.util.List;

public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo add(String title) {
        return todoRepository.save(title);
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo complete(Long id) {
        Todo todo = findById(id);
        todo.complete();
        return todo;
    }

    public void delete(Long id) {
        findById(id);
        todoRepository.deleteById(id);
    }

    public List<Todo> search(String keyword) {
        return todoRepository.findAll().stream()
                .filter(todo -> todo.matchesKeyword(keyword))
                .toList();
    }
}
