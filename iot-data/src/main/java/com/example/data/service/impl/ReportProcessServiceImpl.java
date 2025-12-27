package com.example.data.service.impl;

import com.example.data.dto.DeviceReportMsg;
import com.example.data.pojo.DeviceData;
import com.example.data.service.AlarmService;
import com.example.data.service.DeviceDataService;
import com.example.data.service.DeviceOnlineService;
import com.example.data.service.ReportProcessService;
import com.example.data.feign.DeviceClient;
import com.example.data.mapper.DeviceDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportProcessServiceImpl implements ReportProcessService {

    private final DeviceHeartbeatRedis heartbeatRedis; // 你已有：1秒幂等（可保留）
    private final DeviceDataMapper deviceDataMapper;
    private final DeviceClient deviceClient;
    private final DeviceOnlineService onlineService;
    private final AlarmService alarmService;
    private final DeviceDataService deviceDataService;
    @Value("${iot.device.offline-minutes}")
    private long offlineMinutes;

    @Override
    public void process(DeviceReportMsg msg) {
        DeviceData data = new DeviceData();
        data.setDeviceId(msg.getDeviceId());
        data.setTemp(msg.getTemp());
        data.setHumi(msg.getHumi());
        data.setBattery(msg.getBattery());
        // createTime 由SQL NOW() 或实体字段自动填充，看你原逻辑
        deviceDataService.report(data);

        log.info("[report-mq] ok deviceId={}, msgId={}, ip={}", msg.getDeviceId(), msg.getMsgId(), msg.getIp());
    }
}