package com.example.data.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class DeviceHeartbeatRedis {

    private final StringRedisTemplate redisTemplate;

    public DeviceHeartbeatRedis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String HB_KEY_PREFIX = "iot:hb:";
    private static final String TIMEOUT_ZSET = "iot:timeout:zset";
    private static final String HB_LOCK_PREFIX = "iot:hb:lock:";

    /** 上报：刷新心跳 + 更新超时ZSET */
    public boolean touch(Long deviceId, long offlineMinutes) {
        String dedupKey = HB_LOCK_PREFIX + deviceId;

        // 1秒内只允许一次（幂等/限流）
        Boolean first = redisTemplate.opsForValue()
                .setIfAbsent(dedupKey, "1", java.time.Duration.ofSeconds(1));

        if (!Boolean.TRUE.equals(first)) {
            return false;
        }
        String hbKey = HB_KEY_PREFIX + deviceId;

        long now = System.currentTimeMillis();
        long expireAt = now + Duration.ofMinutes(offlineMinutes).toMillis();

        // 1) 心跳 key（带 TTL）
        redisTemplate.opsForValue().set(hbKey, String.valueOf(now), Duration.ofMinutes(offlineMinutes));

        // 2) 超时 zset：score = 到期时间戳
        redisTemplate.opsForZSet().add(TIMEOUT_ZSET, String.valueOf(deviceId), expireAt);
        return true;
    }

    /** 获取超时的设备id（按 score <= now） */
    public Set<String> getTimeoutDeviceIds(long nowMillis, int limit) {
        return redisTemplate.opsForZSet().rangeByScore(TIMEOUT_ZSET, 0, nowMillis, 0, limit);
    }

    /** 从超时集合移除（处理完离线后删） */
    public void removeFromTimeoutZset(Set<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) return;
        redisTemplate.opsForZSet().remove(TIMEOUT_ZSET, deviceIds.toArray());
    }

    /** 可选：判断这个设备现在是否仍然“有心跳”（hbKey 还存在） */
    public boolean isAlive(Long deviceId) {
        String hbKey = HB_KEY_PREFIX + deviceId;
        Boolean exists = redisTemplate.hasKey(hbKey);
        return Boolean.TRUE.equals(exists);
    }
}