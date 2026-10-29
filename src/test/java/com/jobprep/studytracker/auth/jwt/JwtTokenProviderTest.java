package com.jobprep.studytracker.auth.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    @Test
    void 서명이_1바이트라도_변조된_토큰은_상수시간_검증에서_거부된다() {
        String token = jwtTokenProvider.createAccessToken(7L, "sec@example.com");
        char lastChar = token.charAt(token.length() - 1);
        char tamperedChar = (lastChar == 'A') ? 'B' : 'A';
        String tamperedToken = token.substring(0, token.length() - 1) + tamperedChar;

        assertThat(jwtTokenProvider.isValidToken(tamperedToken)).isFalse();
    }

    @Test
    void 페이로드가_변조된_토큰은_서명_불일치로_거부된다() {
        String token = jwtTokenProvider.createAccessToken(1L, "user@example.com");
        String[] parts = token.split("\\.");
        String forgedPayload = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString("{\"sub\":\"999\",\"email\":\"admin@example.com\",\"iat\":1,\"exp\":4102444800}"
                        .getBytes(StandardCharsets.UTF_8));
        String forgedToken = parts[0] + "." + forgedPayload + "." + parts[2];

        assertThat(jwtTokenProvider.isValidToken(forgedToken)).isFalse();
    }

    @Test
    void 생성된_토큰에서_userId와_email을_정확히_추출한다() {
        String token = jwtTokenProvider.createAccessToken(42L, "dev\"quote@example.com");

        assertThat(jwtTokenProvider.isValidToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(42L);
        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("dev\"quote@example.com");
    }
}
