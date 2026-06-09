package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.repository.SpringDataTodoJpaRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoStatsCacheTest {
    @Autowired
    TodoService todoService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SpringDataTodoJpaRepository todoJpaRepository;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        var cache = cacheManager.getCache("todoStats");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void todo_통계는_캐시된다() {
        User user = userRepository.save(new User("cache@example.com", "password", "캐시 사용자"));
        todoJpaRepository.save(new Todo("첫 번째 Todo", user));

        var first = todoService.findStats(user.getId());

        todoJpaRepository.save(new Todo("두 번째 Todo", user));

        var second = todoService.findStats(user.getId());

        assertThat(first.totalCount()).isEqualTo(1);
        assertThat(second.totalCount()).isEqualTo(1);
    }

    @Test
    void todo가_변경되면_통계_캐시가_삭제된다() {
        User user = userRepository.save(new User("evict@example.com", "password", "캐시 삭제 사용자"));

        todoService.add(user.getId(), "첫 번째 Todo");
        var first = todoService.findStats(user.getId());

        todoService.add(user.getId(), "두 번째 Todo");
        var second = todoService.findStats(user.getId());

        assertThat(first.totalCount()).isEqualTo(1);
        assertThat(second.totalCount()).isEqualTo(2);
    }
}
