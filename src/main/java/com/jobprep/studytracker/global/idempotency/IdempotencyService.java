package com.jobprep.studytracker.global.idempotency;

import com.jobprep.studytracker.global.exception.DuplicateRequestException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class IdempotencyService {
    private static final Duration TTL = Duration.ofMinutes(5);

    private final IdempotencyStore idempotencyStore;

    public IdempotencyService(IdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    public <T> T executeOnce(Long userId, String action, String idempotencyKey, Supplier<T> supplier) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return supplier.get();
        }

        String key = "idempotency:%s:%d:%s".formatted(action, userId, idempotencyKey);
        if (!idempotencyStore.tryStart(key, TTL)) {
            throw new DuplicateRequestException();
        }

        try {
            T result = supplier.get();
            idempotencyStore.markDone(key, TTL);
            return result;
        } catch (RuntimeException e) {
            idempotencyStore.delete(key);
            throw e;
        }
    }
}
