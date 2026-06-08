package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo add(String title) {
        return todoRepository.save(title);
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

    public Todo updateTitle(Long id, String title) {
        Todo todo = findById(id);
        todo.changeTitle(title);
        return todo;
    }

    public Todo uncomplete(Long id) {
        Todo todo = findById(id);
        todo.uncomplete();
        return todo;
    }

    public List<Todo> findAll(String keyword, Boolean completed) {
        return todoRepository.findAll().stream()
                .filter(todo -> keyword == null || keyword.isBlank() || todo.matchesKeyword(keyword))
                .filter(todo -> completed == null || todo.isCompleted() == completed)
                .toList();
    }
}
