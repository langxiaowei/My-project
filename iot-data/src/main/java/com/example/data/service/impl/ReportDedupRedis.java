package com.example.data.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ReportDedupRedis {

    private static final String PREFIX = "iot:report:dedup:";

    private final StringRedisTemplate redisTemplate;

    @Value("${iot.report.dedup-seconds:60}")
    private long dedupSeconds;

    public ReportDedupRedis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allow(String msgId) {
        if (msgId == null || msgId.isBlank()) return true; // 没msgId就不拦（不推荐）
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(PREFIX + msgId, "1", Duration.ofSeconds(dedupSeconds));
        return Boolean.TRUE.equals(ok);
    }
}