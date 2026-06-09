package com.jobprep.studytracker.global.idempotency;

import java.time.Duration;

public interface IdempotencyStore {
    boolean tryStart(String key, Duration ttl);

    void markDone(String key, Duration ttl);

    void delete(String key);
}
