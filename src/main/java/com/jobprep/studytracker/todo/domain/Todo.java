package com.jobprep.studytracker.todo.domain;

public class Todo {
    private final Long id;
    private String title;
    private boolean completed;

    public Todo(Long id, String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("할일 제목은 비어있을 수 없습니다.");
        }

        this.id = id;
        this.title = title;
        this.completed = false;
    }

    public void complete() {
        this.completed = true;
    }

    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        return this.title.toLowerCase().contains(keyword.toLowerCase());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void changeTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("할일 제목은 비어있을 수 없습니다.");
        }

        this.title = title;
    }

    public void uncomplete() {
        this.completed = false;
    }
}
