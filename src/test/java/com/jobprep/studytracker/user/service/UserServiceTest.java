package com.jobprep.studytracker.user.service;

import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원가입_시_비밀번호를_암호화해서_저장한다() {
        userService.signup("test@example.com", "password123", "테스터");

        User user = userRepository.findByEmail("test@example.com")
                .orElseThrow();

        assertThat(user.getPassword()).isNotEqualTo("password123");
        assertThat(user.getPassword()).startsWith("$2");
    }
}
