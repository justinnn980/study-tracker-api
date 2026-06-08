package com.jobprep.studytracker.todo;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.MemoryTodoRepository;
import com.jobprep.studytracker.todo.service.TodoService;

import java.util.List;
import java.util.Scanner;

public class TodoConsoleApplication {
    private final TodoService todoService;
    private final Scanner scanner;

    public TodoConsoleApplication(TodoService todoService, Scanner scanner) {
        this.todoService = todoService;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        TodoService todoService = new TodoService(new MemoryTodoRepository());
        TodoConsoleApplication application = new TodoConsoleApplication(todoService, new Scanner(System.in));
        application.run();
    }

    public void run() {
        while (true) {
            printMenu();
            String menu = scanner.nextLine();

            try {
                if ("1".equals(menu)) {
                    add();
                } else if ("2".equals(menu)) {
                    printTodos(todoService.findAll());
                } else if ("3".equals(menu)) {
                    complete();
                } else if ("4".equals(menu)) {
                    delete();
                } else if ("5".equals(menu)) {
                    search();
                } else if ("0".equals(menu)) {
                    System.out.println("종료합니다.");
                    return;
                } else {
                    System.out.println("없는 메뉴입니다.");
                }
            } catch (IllegalArgumentException | TodoNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. 할 일 추가");
        System.out.println("2. 전체 조회");
        System.out.println("3. 완료 처리");
        System.out.println("4. 삭제");
        System.out.println("5. 검색");
        System.out.println("0. 종료");
        System.out.print("메뉴 선택: ");
    }

    private void add() {
        System.out.print("제목: ");
        Todo todo = todoService.add(scanner.nextLine());
        System.out.println("추가됨: " + format(todo));
    }

    private void complete() {
        Long id = readId();
        Todo todo = todoService.complete(id);
        System.out.println("완료됨: " + format(todo));
    }

    private void delete() {
        Long id = readId();
        todoService.delete(id);
        System.out.println("삭제됨: id=" + id);
    }

    private void search() {
        System.out.print("검색어: ");
        printTodos(todoService.search(scanner.nextLine()));
    }

    private Long readId() {
        System.out.print("id: ");
        return Long.parseLong(scanner.nextLine());
    }

    private void printTodos(List<Todo> todos) {
        if (todos.isEmpty()) {
            System.out.println("할 일이 없습니다.");
            return;
        }

        for (Todo todo : todos) {
            System.out.println(format(todo));
        }
    }

    private String format(Todo todo) {
        String status = todo.isCompleted() ? "완료" : "미완료";
        return "[" + todo.getId() + "] " + todo.getTitle() + " - " + status;
    }
}
