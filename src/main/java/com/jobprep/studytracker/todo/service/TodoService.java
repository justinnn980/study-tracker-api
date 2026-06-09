package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.TodoRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
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

    public Todo add(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        return todoRepository.save(title, user);
    }

    @Transactional(readOnly = true)
    public Todo findById(Long userId, Long id) {
        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo complete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todo.complete();
        return todo;
    }

    public void delete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todoRepository.delete(todo);
    }

    public Todo updateTitle(Long userId, Long id, String title) {
        Todo todo = findById(userId, id);
        todo.changeTitle(title);
        return todo;
    }

    public Todo uncomplete(Long userId, Long id) {
        Todo todo = findById(userId, id);
        todo.uncomplete();
        return todo;
    }

    @Transactional(readOnly = true)
    public List<Todo> findAll(Long userId, String keyword, Boolean completed) {
        return todoRepository.findAll(userId, keyword, completed);
    }
}
