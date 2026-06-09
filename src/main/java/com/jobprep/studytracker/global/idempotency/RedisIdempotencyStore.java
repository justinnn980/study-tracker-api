package com.jobprep.studytracker.global.idempotency;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("!test")
public class RedisIdempotencyStore implements IdempotencyStore {
    private static final String PROCESSING = "PROCESSING";
    private static final String DONE = "DONE";

    private final StringRedisTemplate redisTemplate;

    public RedisIdempotencyStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryStart(String key, Duration ttl) {
        Boolean saved = redisTemplate.opsForValue().setIfAbsent(key, PROCESSING, ttl);
        return Boolean.TRUE.equals(saved);
    }

    @Override
    public void markDone(String key, Duration ttl) {
        redisTemplate.opsForValue().set(key, DONE, ttl);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
