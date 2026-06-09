package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.dto.TodoStatsResponse;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.TodoRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.exception.UserNotFoundException;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(
            TodoRepository todoRepository,
            UserRepository userRepository
    ) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "todoStats", key = "#userId")
    public Todo add(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return todoRepository.save(title, user);
    }

    @Transactional(readOnly = true)
    public Todo findById(Long userId, Long id) {
        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @CacheEvict(value = "todoStats", key = "#userId")
    public Todo complete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todo.complete();
        return todo;
    }

    @CacheEvict(value = "todoStats", key = "#userId")
    public void delete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todoRepository.delete(todo);
    }

    @CacheEvict(value = "todoStats", key = "#userId")
    public Todo updateTitle(Long userId, Long id, String title) {
        Todo todo = findById(userId, id);
        todo.changeTitle(title);
        return todo;
    }

    @CacheEvict(value = "todoStats", key = "#userId")
    public Todo uncomplete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todo.uncomplete();
        return todo;
    }

    @Transactional(readOnly = true)
    public List<Todo> findAll(Long userId, String keyword, Boolean completed) {
        return todoRepository.findAll(userId, keyword, completed);
    }

    @Cacheable(value = "todoStats", key = "#userId")
    @Transactional(readOnly = true)
    public TodoStatsResponse findStats(Long userId) {
        long totalCount = todoRepository.countByUserId(userId);
        long completedCount = todoRepository.countByUserIdAndCompleted(userId, true);

        return TodoStatsResponse.of(totalCount, completedCount);
    }
}
