package com.jobprep.studytracker.todo.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.repository.SpringDataTodoJpaRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.show-sql=false"
})
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

    @Autowired
    EntityManagerFactory entityManagerFactory;

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

    @Test
    void todo_완료_처리_시_통계_캐시가_즉시_무효화된다() {
        User user = userRepository.save(new User("complete@example.com", "password", "완료 사용자"));
        Todo todo = todoService.add(user.getId(), "완료 대상 Todo");
        assertThat(todoService.findStats(user.getId()).completedCount()).isZero();

        todoService.complete(user.getId(), todo.getId());

        var afterComplete = todoService.findStats(user.getId());
        assertThat(afterComplete.completedCount()).isEqualTo(1);
        assertThat(afterComplete.activeCount()).isZero();
    }

    @Test
    void todo_완료_취소_시_통계_캐시가_즉시_무효화된다() {
        User user = userRepository.save(new User("uncomplete@example.com", "password", "완료 취소 사용자"));
        Todo todo = todoService.add(user.getId(), "완료 취소 대상 Todo");
        todoService.complete(user.getId(), todo.getId());
        assertThat(todoService.findStats(user.getId()).completedCount()).isEqualTo(1);

        todoService.uncomplete(user.getId(), todo.getId());

        var afterUncomplete = todoService.findStats(user.getId());
        assertThat(afterUncomplete.completedCount()).isZero();
        assertThat(afterUncomplete.activeCount()).isEqualTo(1);
    }

    @Test
    void todo_제목_수정_시_통계_캐시가_즉시_무효화된다() {
        User user = userRepository.save(new User("update@example.com", "password", "수정 사용자"));
        Todo todo = todoService.add(user.getId(), "원래 제목");
        todoService.findStats(user.getId());

        todoJpaRepository.save(new Todo("외부 반영 확인용 Todo", user));
        todoService.updateTitle(user.getId(), todo.getId(), "변경된 제목");

        assertThat(todoService.findStats(user.getId()).totalCount()).isEqualTo(2);
    }

    @Test
    void todo_삭제_시_통계_캐시가_즉시_무효화된다() {
        User user = userRepository.save(new User("delete@example.com", "password", "삭제 사용자"));
        Todo todo = todoService.add(user.getId(), "삭제 대상 Todo");
        assertThat(todoService.findStats(user.getId()).totalCount()).isEqualTo(1);

        todoService.delete(user.getId(), todo.getId());

        assertThat(todoService.findStats(user.getId()).totalCount()).isZero();
    }

    @Test
    void 반복_통계_조회_100회_시_캐시_적중으로_DB_쿼리가_200회에서_2회로_99퍼센트_감소한다() {
        User user = userRepository.save(new User("bench@example.com", "password", "벤치마크 사용자"));
        List<Todo> batch = new ArrayList<>(5_000);
        for (int i = 0; i < 5_000; i++) {
            Todo todo = new Todo("Todo " + i, user);
            if (i % 2 == 0) {
                todo.complete();
            }
            batch.add(todo);
        }
        todoJpaRepository.saveAll(batch);
        clearCache();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        long missStartNanos = System.nanoTime();
        var missResult = todoService.findStats(user.getId());
        long missElapsedNanos = System.nanoTime() - missStartNanos;
        long queriesAfterFirstCall = statistics.getPrepareStatementCount();

        long hitStartNanos = System.nanoTime();
        for (int i = 0; i < 99; i++) {
            var hitResult = todoService.findStats(user.getId());
            assertThat(hitResult.totalCount()).isEqualTo(5_000);
            assertThat(hitResult.completedCount()).isEqualTo(2_500);
        }
        long hitTotalElapsedNanos = System.nanoTime() - hitStartNanos;
        long totalQueriesAfter100Calls = statistics.getPrepareStatementCount();

        double missMs = missElapsedNanos / 1_000_000.0;
        double avgHitMs = (hitTotalElapsedNanos / 99.0) / 1_000_000.0;
        System.out.printf(
                "[STATS_CACHE_BENCH] 5,000 rows | 1st (cache miss)=%.3f ms (queries=%d) | 99x avg (cache hit)=%.4f ms (total queries after 100 calls=%d)%n",
                missMs,
                queriesAfterFirstCall,
                avgHitMs,
                totalQueriesAfter100Calls
        );

        assertThat(missResult.totalCount()).isEqualTo(5_000);
        assertThat(missResult.completedCount()).isEqualTo(2_500);
        assertThat(queriesAfterFirstCall).isEqualTo(2);
        assertThat(totalQueriesAfter100Calls).isEqualTo(2);
    }
}
