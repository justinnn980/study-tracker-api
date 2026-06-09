package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpringDataTodoJpaRepositoryTest {
    @Autowired
    SpringDataTodoJpaRepository todoRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void userId로_자신의_todo만_조회한다() {
        User owner = userRepository.save(new User("owner@example.com", "password", "소유자"));
        User other = userRepository.save(new User("other@example.com", "password", "다른 사용자"));

        Todo todo = todoRepository.save(new Todo("소유자의 Todo", owner));

        assertThat(todoRepository.findByIdAndUserId(todo.getId(), owner.getId())).isPresent();
        assertThat(todoRepository.findByIdAndUserId(todo.getId(), other.getId())).isEmpty();
    }

    @Test
    void 목록_조회는_사용자별로_분리된다() {
        User owner = userRepository.save(new User("owner@example.com", "password", "소유자"));
        User other = userRepository.save(new User("other@example.com", "password", "다른 사용자"));

        todoRepository.save(new Todo("소유자의 Todo", owner));
        todoRepository.save(new Todo("다른 사용자의 Todo", other));

        assertThat(todoRepository.findByUserIdOrderByIdAsc(owner.getId()))
                .extracting(Todo::getTitle)
                .containsExactly("소유자의 Todo");
    }
}
