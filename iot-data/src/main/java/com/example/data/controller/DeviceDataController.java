package com.example.data.controller;
import com.example.data.dto.*;
import com.example.data.feign.DeviceClient;
import com.example.data.dto.*;
import com.example.data.mq.ReportProducer;
import com.example.data.pojo.DeviceData;
import com.example.data.service.DeviceDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;

    private final DeviceClient deviceClient;
    private final ReportProducer reportProducer;
    // 设备数据上报
    @PostMapping("/report")
    public Map<String, Object> report(@RequestBody DeviceData data,
                                      HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();

        DeviceReportMsg msg = new DeviceReportMsg();
        msg.setMsgId(java.util.UUID.randomUUID().toString());
        msg.setDeviceId(data.getDeviceId());
        msg.setTemp(data.getTemp());
        msg.setHumi(data.getHumi());
        msg.setBattery(data.getBattery());
        msg.setIp(ip);
        msg.setTs(System.currentTimeMillis());

        reportProducer.send(msg);

        return java.util.Map.of("message", "已接收(异步处理)", "msgId", msg.getMsgId());
//        deviceDataService.report(data);
    }

    // 某设备的历史数据
    @GetMapping("/history/{deviceId}")
    public List<DeviceData> historybyid(@PathVariable("deviceId") Long deviceId) {
        return deviceDataService.historybyid(deviceId);
    }



    // 某设备最近 N 条数据，给图表用
    @GetMapping("/history/{deviceId}/latest")
    public List<DataRecord> latestHistory(
            @PathVariable("deviceId") Long deviceId,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        return deviceDataService.latestHistory(deviceId, limit);
    }

    @GetMapping("/devices")
    public R<PageDTO<DeviceRowDTO>> devices(@RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return R.ok(deviceDataService.devices(page, size));
    }

    @GetMapping("/history")
    public List<DeviceData> historyAll(
            @RequestParam(value = "limit", defaultValue = "200") Integer limit) {
        return deviceDataService.historyAll(limit);
    }

    @GetMapping("/device/{id}")
    public R<DeviceDetailDTO> deviceDetail(@PathVariable("id") Long id) {
        return R.ok(deviceDataService.deviceDetail(id));
    }

    @GetMapping("/history/metric")
    public List<DataPointDTO> historyMetric(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "metric", required = false) String metric,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        return deviceDataService.historyMetric(deviceId, metric, limit);
    }

    @GetMapping("/history/range")
    public List<DataPointDTO> historyRange(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "metric", required = false) String metric,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime to
    ) {
        return deviceDataService.historyRange(deviceId, metric, from, to);
    }
}