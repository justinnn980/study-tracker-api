package com.jobprep.studytracker.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtTokenProvider {
    private static final String HMAC_SHA256 = "HmacSHA256";

    private final String secret;
    private final long accessTokenExpirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis
    ) {
        this.secret = secret;
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    }

    public String createAccessToken(Long userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(accessTokenExpirationMillis);

        String header = """
                {"alg":"HS256","typ":"JWT"}
                """.strip();
        String payload = String.format(
                "{\"sub\":\"%s\",\"email\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userId,
                escapeJson(email),
                now.getEpochSecond(),
                expiresAt.getEpochSecond()
        );

        String unsignedToken = base64UrlEncode(header) + "." + base64UrlEncode(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    private String base64UrlEncode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(key);

            byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("JWT 서명 생성에 실패했습니다.", e);
        }
    }
}
