package com.jobprep.studytracker.todo.domain;

import com.jobprep.studytracker.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "todos",
        indexes = {
                @Index(name = "idx_todos_user_id_id", columnList = "user_id, id"),
                @Index(name = "idx_todos_user_id_completed_id", columnList = "user_id, completed, id")
        }
)
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Todo() {
    }

    public Todo(String title, User user) {
        validateTitle(title);
        validateUser(user);

        this.title = title;
        this.user = user;
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

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("할일 제목은 비어있을 수 없습니다.");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자는 비어 있을 수 없습니다.");
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

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void uncomplete() {
        this.completed = false;
    }
}
