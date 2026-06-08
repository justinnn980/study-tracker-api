package com.jobprep.studytracker.todo.controller;

import com.jobprep.studytracker.todo.dto.CreateTodoRequest;
import com.jobprep.studytracker.todo.dto.UpdateTodoRequest;
import com.jobprep.studytracker.todo.dto.TodoResponse;
import com.jobprep.studytracker.todo.service.TodoService;
import com.jobprep.studytracker.todo.domain.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.add(request.title());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TodoResponse.from(todo));
    }

    @GetMapping
    public List<TodoResponse> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean completed
    ) {
        return todoService.findAll(keyword, completed).stream()
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public TodoResponse updateTitle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTodoRequest request
    ) {
        return TodoResponse.from(todoService.updateTitle(id, request.title()));
    }

    @PatchMapping("/{id}/uncomplete")
    public TodoResponse uncomplete(@PathVariable Long id) {
        return TodoResponse.from(todoService.uncomplete(id));
    }
}
