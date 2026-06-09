package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.exception.TodoNotFoundException;
import com.jobprep.studytracker.todo.repository.TodoRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TodoServiceTest {
    private final TodoRepository todoRepository = mock(TodoRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TodoService todoService = new TodoService(todoRepository, userRepository);

    @Test
    void todo_생성_시_사용자와_함께_저장한다() {
        User user = new User("test@example.com", "password", "테스터");
        Todo savedTodo = new Todo("Java 공부", user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.save("Java 공부", user)).thenReturn(savedTodo);

        Todo result = todoService.add(1L, "Java 공부");

        assertThat(result).isSameAs(savedTodo);
        verify(todoRepository).save("Java 공부", user);
    }

    @Test
    void 다른_사용자의_todo는_조회할_수_없다() {
        when(todoRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(2L, 1L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void 삭제할_때도_사용자_소유_todo만_삭제한다() {
        User user = new User("test@example.com", "password", "테스터");
        Todo todo = new Todo("삭제할 Todo", user);

        when(todoRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(todo));

        todoService.delete(1L, 1L);

        verify(todoRepository).delete(todo);
    }
}
