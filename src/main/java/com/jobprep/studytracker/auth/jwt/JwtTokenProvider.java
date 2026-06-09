package com.jobprep.studytracker.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtTokenProvider {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Pattern SUB_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\"email\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

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
                Locale.ROOT,
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


    public boolean isValidToken(String token) {
        try {
            parsePayload(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return parsePayload(token).userId();
    }

    public String getEmail(String token) {
        return parsePayload(token).email();
    }

    private JwtPayload parsePayload(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException("잘못된 JWT 형식입니다.");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);

            if (!isSameSignature(expectedSignature, parts[2])) {
                throw new IllegalArgumentException("JWT 서명이 올바르지 않습니다.");
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);

            long expiresAt = extractLong(payloadJson, EXP_PATTERN, "exp");
            if (Instant.now().getEpochSecond() >= expiresAt) {
                throw new IllegalArgumentException("JWT가 만료되었습니다.");
            }

            return new JwtPayload(
                    Long.valueOf(extractString(payloadJson, SUB_PATTERN, "sub")),
                    extractString(payloadJson, EMAIL_PATTERN, "email")
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT를 검증하지 못했습니다.", e);
        }
    }

    private boolean isSameSignature(String expectedSignature, String actualSignature) {
        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                actualSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String extractString(String json, Pattern pattern, String fieldName) {
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("JWT payload에 " + fieldName + " 값이 없습니다.");
        }

        return unescapeJson(matcher.group(1));
    }

    private long extractLong(String json, Pattern pattern, String fieldName) {
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("JWT payload에 " + fieldName + " 값이 없습니다.");
        }

        return Long.parseLong(matcher.group(1));
    }

    private String unescapeJson(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            if (escaped) {
                result.append(current);
                escaped = false;
                continue;
            }

            if (current == '\\') {
                escaped = true;
                continue;
            }

            result.append(current);
        }

        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }

    private record JwtPayload(
            Long userId,
            String email
    ) {
    }
}
