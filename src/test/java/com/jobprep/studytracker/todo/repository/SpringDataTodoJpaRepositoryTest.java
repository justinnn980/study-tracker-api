package com.jobprep.studytracker.todo.repository;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void 복합_인덱스_조건인_사용자별_완료여부_필터와_통계_집계가_정확히_동작한다() {
        User owner = userRepository.save(new User("idx-owner@example.com", "password", "인덱스 소유자"));
        User other = userRepository.save(new User("idx-other@example.com", "password", "타 사용자"));

        List<Todo> todos = new ArrayList<>(1_000);
        for (int i = 0; i < 600; i++) {
            Todo todo = new Todo("Owner Todo " + i, owner);
            if (i < 240) {
                todo.complete();
            }
            todos.add(todo);
        }
        for (int i = 0; i < 400; i++) {
            Todo todo = new Todo("Other Todo " + i, other);
            todo.complete();
            todos.add(todo);
        }
        todoRepository.saveAll(todos);

        assertThat(todoRepository.countByUserId(owner.getId())).isEqualTo(600);
        assertThat(todoRepository.countByUserIdAndCompleted(owner.getId(), true)).isEqualTo(240);
        assertThat(todoRepository.findByUserIdAndCompletedOrderByIdAsc(owner.getId(), false)).hasSize(360);
    }

    @Test
    void 키워드와_완료여부_복합_조건_조회가_사용자별로_격리된다() {
        User owner = userRepository.save(new User("kw-owner@example.com", "password", "키워드 소유자"));
        User other = userRepository.save(new User("kw-other@example.com", "password", "타 사용자"));

        Todo doneSpring = new Todo("Spring Security 학습", owner);
        doneSpring.complete();
        Todo activeSpring = new Todo("Spring JPA 최적화", owner);
        Todo otherDoneSpring = new Todo("Spring Redis 실습", other);
        otherDoneSpring.complete();

        todoRepository.saveAll(List.of(doneSpring, activeSpring, otherDoneSpring));

        List<Todo> result = todoRepository.findByUserIdAndTitleContainingIgnoreCaseAndCompletedOrderByIdAsc(
                owner.getId(),
                "spring",
                true
        );

        assertThat(result)
                .extracting(Todo::getTitle)
                .containsExactly("Spring Security 학습");
    }
}
