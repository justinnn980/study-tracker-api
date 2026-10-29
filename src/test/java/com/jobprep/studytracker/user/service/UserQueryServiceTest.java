package com.jobprep.studytracker.user.service;

import com.jobprep.studytracker.todo.domain.Todo;
import com.jobprep.studytracker.todo.repository.SpringDataTodoJpaRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.dto.UserTodoSummaryResponse;
import com.jobprep.studytracker.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class UserQueryServiceTest {
    @Autowired
    UserQueryService userQueryService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SpringDataTodoJpaRepository todoRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    void 일반_조회는_lazy_loading으로_N_plus_1이_발생할_수_있다() {
        saveUserWithTodo("user1@example.com", "사용자1", "Todo 1");
        saveUserWithTodo("user2@example.com", "사용자2", "Todo 2");
        flushAndClear();

        Statistics statistics = statistics();
        statistics.clear();

        List<UserTodoSummaryResponse> responses =
                userQueryService.findUserTodoSummariesWithNPlusOneForStudy();

        assertThat(responses).hasSize(2);
        assertThat(statistics.getPrepareStatementCount()).isGreaterThan(1);
    }

    @Test
    void fetch_join으로_N_plus_1을_해결한다() {
        saveUserWithTodo("user1@example.com", "사용자1", "Todo 1");
        saveUserWithTodo("user2@example.com", "사용자2", "Todo 2");
        flushAndClear();

        Statistics statistics = statistics();
        statistics.clear();

        List<UserTodoSummaryResponse> responses =
                userQueryService.findUserTodoSummaries();

        assertThat(responses).hasSize(2);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void 사용자_50명_조회_시_fetch_join으로_SQL_쿼리가_51회에서_1회로_98퍼센트_감소한다() {
        List<Todo> todos = new ArrayList<>(250);
        for (int i = 1; i <= 50; i++) {
            User user = userRepository.save(new User("u" + i + "@example.com", "password", "사용자" + i));
            for (int j = 1; j <= 5; j++) {
                todos.add(new Todo("Todo " + i + "-" + j, user));
            }
        }
        todoRepository.saveAll(todos);
        flushAndClear();

        Statistics statistics = statistics();
        statistics.clear();

        long nPlusOneStart = System.nanoTime();
        List<UserTodoSummaryResponse> nPlusOneResponses =
                userQueryService.findUserTodoSummariesWithNPlusOneForStudy();
        long nPlusOneElapsedNanos = System.nanoTime() - nPlusOneStart;
        long nPlusOneQueries = statistics.getPrepareStatementCount();

        flushAndClear();
        statistics.clear();

        long fetchJoinStart = System.nanoTime();
        List<UserTodoSummaryResponse> fetchJoinResponses =
                userQueryService.findUserTodoSummaries();
        long fetchJoinElapsedNanos = System.nanoTime() - fetchJoinStart;
        long fetchJoinQueries = statistics.getPrepareStatementCount();

        System.out.printf(
                "[FETCH_JOIN_BENCH] 50 users (250 todos) | N+1=%.3f ms (queries=%d) | FetchJoin=%.3f ms (queries=%d)%n",
                nPlusOneElapsedNanos / 1_000_000.0,
                nPlusOneQueries,
                fetchJoinElapsedNanos / 1_000_000.0,
                fetchJoinQueries
        );

        assertThat(nPlusOneResponses).hasSize(50);
        assertThat(fetchJoinResponses).hasSize(50);
        assertThat(nPlusOneQueries).isEqualTo(51);
        assertThat(fetchJoinQueries).isEqualTo(1);
    }

    private void saveUserWithTodo(String email, String name, String title) {
        User user = userRepository.save(new User(email, "password", name));
        todoRepository.save(new Todo(title, user));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Statistics statistics() {
        return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }
}
