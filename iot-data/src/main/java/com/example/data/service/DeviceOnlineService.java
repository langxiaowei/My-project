package com.example.data.service;

public interface DeviceOnlineService {
    void heartbeat(long deviceId);              // 上报时调用
    java.util.List<Long> findTimeoutDevices(long cutoffMillis);
    void removeDevices(java.util.List<Long> deviceIds);
}