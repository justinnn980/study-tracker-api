package com.jobprep.studytracker.auth.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenProviderTest {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void accessToken_생성_성공() {
        String token = jwtTokenProvider.createAccessToken(1L, "test@example.com");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 잘못된_accessToken은_유효하지_않다() {
        assertThat(jwtTokenProvider.isValidToken("wrong-token")).isFalse();
    }

    @Test
    void 만료된_accessToken은_유효하지_않다() {
        JwtTokenProvider expiredTokenProvider = new JwtTokenProvider("test-secret", -1000);

        String token = expiredTokenProvider.createAccessToken(1L, "test@example.com");

        assertThat(expiredTokenProvider.isValidToken(token)).isFalse();
    }
}
