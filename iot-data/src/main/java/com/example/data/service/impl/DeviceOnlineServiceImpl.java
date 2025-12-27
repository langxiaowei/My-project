package com.example.data.service.impl;

import com.example.data.service.DeviceOnlineService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceOnlineServiceImpl implements DeviceOnlineService {

    private static final String KEY = "iot:device:lastReport";

    private final org.springframework.data.redis.core.StringRedisTemplate redis;

    public DeviceOnlineServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void heartbeat(long deviceId) {
        long now = System.currentTimeMillis();
        redis.opsForZSet().add(KEY, String.valueOf(deviceId), now);
    }

    @Override
    public List<Long> findTimeoutDevices(long cutoffMillis) {
        var set = redis.opsForZSet().rangeByScore(KEY, 0, cutoffMillis);
        if (set == null || set.isEmpty()) return java.util.Collections.emptyList();
        return set.stream().map(Long::valueOf).toList();
    }

    @Override
    public void removeDevices(List<Long> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) return;
        redis.opsForZSet().remove(KEY, deviceIds.stream().map(String::valueOf).toArray());
    }
}