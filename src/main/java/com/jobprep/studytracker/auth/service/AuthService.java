package com.jobprep.studytracker.auth.service;

import com.jobprep.studytracker.auth.domain.RefreshToken;
import com.jobprep.studytracker.auth.dto.LoginResponse;
import com.jobprep.studytracker.auth.dto.TokenRefreshResponse;
import com.jobprep.studytracker.auth.exception.AuthException;
import com.jobprep.studytracker.auth.exception.ExpiredRefreshTokenException;
import com.jobprep.studytracker.auth.exception.InvalidRefreshTokenException;
import com.jobprep.studytracker.auth.jwt.JwtTokenProvider;
import com.jobprep.studytracker.auth.repository.RefreshTokenRepository;
import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long refreshTokenExpirationDays;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            @Value("${jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Transactional
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = createRefreshToken(user);

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public TokenRefreshResponse refresh(String refreshToken) {
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (savedRefreshToken.isExpired()) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new ExpiredRefreshTokenException();
        }

        User user = savedRefreshToken.getUser();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        return new TokenRefreshResponse(accessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(refreshTokenExpirationDays);

        refreshTokenRepository.save(new RefreshToken(token, user, expiresAt));

        return token;
    }
}
