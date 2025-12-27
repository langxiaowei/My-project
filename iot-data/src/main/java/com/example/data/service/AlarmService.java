package com.example.data.service;

import com.example.data.dto.PageDTO;
import com.example.data.pojo.Alarm;
import com.example.data.pojo.DeviceData;

import java.time.LocalDateTime;

public interface AlarmService {

    void checkAndCreateAlarms(DeviceData data);

    PageDTO<Alarm> list(Long deviceId, String alarmType, Integer level, Integer status,
                        LocalDateTime startTime, LocalDateTime endTime,
                        Integer page, Integer size);

    int ack(Long id);    // status=1
    int close(Long id);  // status=2

    Alarm detail(Long id); // 可选

}