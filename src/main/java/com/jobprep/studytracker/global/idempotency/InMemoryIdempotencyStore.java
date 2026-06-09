package com.jobprep.studytracker.global.idempotency;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("test")
public class InMemoryIdempotencyStore implements IdempotencyStore {
    private final Map<String, Instant> expiresAtByKey = new ConcurrentHashMap<>();

    @Override
    public boolean tryStart(String key, Duration ttl) {
        deleteIfExpired(key);
        Instant expiresAt = Instant.now().plus(ttl);
        return expiresAtByKey.putIfAbsent(key, expiresAt) == null;
    }

    @Override
    public void markDone(String key, Duration ttl) {
        expiresAtByKey.put(key, Instant.now().plus(ttl));
    }

    @Override
    public void delete(String key) {
        expiresAtByKey.remove(key);
    }

    private void deleteIfExpired(String key) {
        Instant expiresAt = expiresAtByKey.get(key);
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            expiresAtByKey.remove(key, expiresAt);
        }
    }
}
