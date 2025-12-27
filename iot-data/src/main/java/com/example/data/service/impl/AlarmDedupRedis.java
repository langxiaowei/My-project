package com.example.data.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AlarmDedupRedis {

    private static final String ALARM_DEDUP_PREFIX = "iot:alarm:dedup:";

    private final StringRedisTemplate redisTemplate;

    @Value("${iot.alarm.dedup-seconds}")
    private long dedupSeconds;

    public AlarmDedupRedis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allow(Long deviceId, String alarmType) {
        String key = ALARM_DEDUP_PREFIX + deviceId + ":" + alarmType;
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(dedupSeconds));
        return Boolean.TRUE.equals(ok);
    }
}