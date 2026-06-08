package com.jobprep.studytracker.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Todo() {
    }

    public Todo(String title) {
        validateTitle(title);
        this.title = title;
        this.completed = false;
    }

    public static Todo createForMemory(Long id, String title) {
        Todo todo = new Todo(title);
        todo.id = id;
        return todo;
    }

    public void complete() {
        this.completed = true;
    }

    public void uncomplete() {
        this.completed = false;
    }

    public void changeTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        return this.title.toLowerCase().contains(keyword.toLowerCase());
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("할일 제목은 비어있을 수 없습니다.");
        }
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
