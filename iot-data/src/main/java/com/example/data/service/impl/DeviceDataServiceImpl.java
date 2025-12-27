package com.example.data.service.impl;
import com.example.data.dto.*;
import com.example.data.enums.MetricEnum;
import com.example.data.feign.DeviceClient;
import com.example.data.service.AlarmService;
import com.example.data.service.DeviceOnlineService;
import com.example.data.dto.*;
import com.example.data.mapper.DeviceDataMapper;
import com.example.data.pojo.DeviceData;
import com.example.data.service.DeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    private final DeviceDataMapper deviceDataMapper;
    private final DeviceClient deviceClient;
    private final DeviceOnlineService onlineService;
    // 注入
    private final DeviceHeartbeatRedis heartbeatRedis;
    private final AlarmService alarmService;

    @Value("${iot.device.offline-minutes}")
    private long offlineMinutes;

    public DeviceDataServiceImpl(DeviceDataMapper deviceDataMapper, DeviceClient deviceClient,
                                 DeviceOnlineService onlineService, DeviceHeartbeatRedis heartbeatRedis,
                                 AlarmService alarmService) {
        this.deviceDataMapper = deviceDataMapper;
        this.deviceClient = deviceClient;
        this.onlineService = onlineService;
        this.heartbeatRedis = heartbeatRedis;
        this.alarmService = alarmService;
    }
    @Override
    public void report(DeviceData data) {

        boolean hb = heartbeatRedis.touch(data.getDeviceId(), offlineMinutes);
        if (!hb) {
            log.info("[report] duplicate in 1s, ignore all, deviceId={}", data.getDeviceId());
            return;
        }

        deviceDataMapper.insert(data); // ✅ 只在抢到1秒资格时入库一次

        deviceClient.markOnline(data.getDeviceId());
        onlineService.heartbeat(data.getDeviceId());
        alarmService.checkAndCreateAlarms(data);
        log.info("[report] heartbeat updated, deviceId={}", data.getDeviceId());
    }

    @Override
    public List<DeviceData> historybyid(Long deviceId) {

        return deviceDataMapper.selecthistoryByDeviceId(deviceId);
    }


    @Override
    public List<DataRecord> latestHistory(Long deviceId, int limit) {
        return deviceDataMapper.selectLatestHistory(deviceId, limit);
    }

    @Override
    public PageDTO<DeviceRowDTO> devices(int page, int size) {

        if (page <= 0) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;

        List<DeviceRowDTO> list =
                deviceDataMapper.selectDevicePage(offset, size);

        Long total = deviceDataMapper.countDevices();

        return new PageDTO<>(total, list);
    }
    @Override
    public List<DeviceData> historyAll(int limit) {
        return deviceDataMapper.selectAllHistory(limit);
    }

    @Override
    public DeviceDetailDTO deviceDetail(Long id) {
        return deviceDataMapper.selectDeviceDetail(id);
    }
    @Override
    public List<DataPointDTO> historyMetric(Long deviceId, String metric, int limit) {
        MetricEnum m = MetricEnum.of(metric);
        List<DataPointDTO> list = deviceDataMapper.selectHistoryPoints(deviceId, m.column(), limit);
        Collections.reverse(list); // 变成时间正序（可选）
        return list;
    }
    @Override
    public List<DataPointDTO> historyRange(Long deviceId, String metric, LocalDateTime from, LocalDateTime to) {
        MetricEnum m = MetricEnum.of(metric);
        return deviceDataMapper.selectHistoryPointsRange(deviceId, m.column(), from, to); // 已 ASC
    }
}