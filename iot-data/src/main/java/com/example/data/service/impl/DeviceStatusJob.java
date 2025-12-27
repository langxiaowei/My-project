package com.example.data.service.impl;

import com.example.data.feign.DeviceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class DeviceStatusJob {

    private final DeviceHeartbeatRedis heartbeatRedis;
    private final DeviceClient deviceClient;

    public DeviceStatusJob(DeviceHeartbeatRedis heartbeatRedis, DeviceClient deviceClient) {
        this.heartbeatRedis = heartbeatRedis;
        this.deviceClient = deviceClient;
    }

    // 每分钟跑一次；每次最多处理 200 个（防止一次离线太多拖垮）
    @Scheduled(cron = "0 */1 * * * ?")
    public void offlineTimeoutDevices() {
        long now = System.currentTimeMillis();

        Set<String> timeoutIds = heartbeatRedis.getTimeoutDeviceIds(now, 200);
        if (timeoutIds == null || timeoutIds.isEmpty()) {
            return;
        }

        // 二次确认：hbKey 不存在才算真的超时（避免 zset 已到期但刚好上报续命）
        List<Long> reallyTimeout = new ArrayList<>();
        for (String idStr : timeoutIds) {
            Long deviceId = Long.valueOf(idStr);
            if (!heartbeatRedis.isAlive(deviceId)) {
                reallyTimeout.add(deviceId);
            }
        }

        if (reallyTimeout.isEmpty()) {
            // 都被续命了，清理掉这批zset旧条目（可选）
            heartbeatRedis.removeFromTimeoutZset(timeoutIds);
            return;
        }

        // 批量离线（调用 iot-device 服务）
        deviceClient.markOfflineBatch(reallyTimeout);

        // 从 zset 移除已处理的
        heartbeatRedis.removeFromTimeoutZset(reallyTimeout.stream().map(String::valueOf).collect(java.util.stream.Collectors.toSet()));

        log.info("Marked offline devices: {}", reallyTimeout);
    }
}