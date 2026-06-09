package com.jobprep.studytracker.auth.service;

import com.jobprep.studytracker.auth.domain.RefreshToken;
import com.jobprep.studytracker.auth.exception.ExpiredRefreshTokenException;
import com.jobprep.studytracker.auth.jwt.JwtTokenProvider;
import com.jobprep.studytracker.auth.repository.RefreshTokenRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private final AuthService authService = new AuthService(
            userRepository,
            refreshTokenRepository,
            passwordEncoder,
            jwtTokenProvider,
            14
    );

    @Test
    void 만료된_refresh_token은_삭제하고_예외를_던진다() {
        User user = new User("test@example.com", "password", "테스터");
        RefreshToken refreshToken = new RefreshToken(
                "expired-refresh-token",
                user,
                LocalDateTime.now().minusDays(1)
        );

        when(refreshTokenRepository.findByToken("expired-refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authService.refresh("expired-refresh-token"))
                .isInstanceOf(ExpiredRefreshTokenException.class);

        verify(refreshTokenRepository).delete(refreshToken);
    }
}
