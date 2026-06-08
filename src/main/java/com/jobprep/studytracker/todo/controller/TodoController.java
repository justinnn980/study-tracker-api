package com.jobprep.studytracker.todo.controller;

import com.jobprep.studytracker.todo.dto.CreateTodoRequest;
import com.jobprep.studytracker.todo.dto.TodoResponse;
import com.jobprep.studytracker.todo.service.TodoService;
import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public TodoResponse create(@Valid @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.add(request.title());
        return TodoResponse.from(todo);
    }

    @GetMapping
    public List<TodoResponse> findAll() {
        return todoService.findAll().stream()
                .map(TodoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TodoResponse findById(@PathVariable Long id) {
        return TodoResponse.from(todoService.findById(id));
    }

    @PatchMapping("/{id}/complete")
    public TodoResponse complete(@PathVariable Long id) {
        return TodoResponse.from(todoService.complete(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }

    @GetMapping("/search")
    public List<TodoResponse> search(@RequestParam String keyword) {
        return todoService.search(keyword).stream()
                .map(TodoResponse::from)
                .toList();
    }


}
