package com.jobprep.studytracker.todo.controller;

import com.jobprep.studytracker.todo.dto.CreateTodoRequest;
import com.jobprep.studytracker.todo.dto.UpdateTodoRequest;
import com.jobprep.studytracker.todo.dto.TodoResponse;
import com.jobprep.studytracker.todo.service.TodoService;
import com.jobprep.studytracker.todo.domain.Todo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todo API", description = "할 일 생성, 조회, 수정, 완료, 삭제 API")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Operation(summary = "Todo 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Todo 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 검증 실패")
    })
    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.add(request.title());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TodoResponse.from(todo));
    }

    @Operation(summary = "Todo 목록 조회")
    @ApiResponse(responseCode = "200", description = "Todo 목록 조회 성공")
    @GetMapping
    public List<TodoResponse> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean completed
    ) {
        return todoService.findAll(keyword, completed).stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Operation(summary = "Todo 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo 조회 성공"),
            @ApiResponse(responseCode = "404", description = "Todo를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public TodoResponse findById(@PathVariable Long id) {
        return TodoResponse.from(todoService.findById(id));
    }

    @Operation(summary = "Todo 완료 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo 완료 처리 성공"),
            @ApiResponse(responseCode = "404", description = "Todo를 찾을 수 없음")
    })
    @PatchMapping("/{id}/complete")
    public TodoResponse complete(@PathVariable Long id) {
        return TodoResponse.from(todoService.complete(id));
    }

    @Operation(summary = "Todo 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Todo를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Todo 제목 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo 제목 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 검증 실패"),
            @ApiResponse(responseCode = "404", description = "Todo를 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    public TodoResponse updateTitle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTodoRequest request
    ) {
        return TodoResponse.from(todoService.updateTitle(id, request.title()));
    }

    @Operation(summary = "Todo 완료 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo 완료 취소 성공"),
            @ApiResponse(responseCode = "404", description = "Todo를 찾을 수 없음")
    })
    @PatchMapping("/{id}/uncomplete")
    public TodoResponse uncomplete(@PathVariable Long id) {
        return TodoResponse.from(todoService.uncomplete(id));
    }
}
