package com.example.notification.service.impl;

import com.example.notification.dto.AlarmEventMsg;
import com.example.notification.mapper.NotificationMapper;
import com.example.notification.pojo.NotificationRecord;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void saveNotification(AlarmEventMsg msg) {
        NotificationRecord record = new NotificationRecord();
        record.setAlarmId(msg.getAlarmId());
        record.setDeviceId(msg.getDeviceId());
        record.setAlarmType(msg.getAlarmType());
        record.setLevel(msg.getLevel());
        record.setContent(msg.getContent());
        record.setStatus(0);

        notificationMapper.insert(record);
        log.info("[notification] saved, id={}, alarmId={}, deviceId={}, type={}",
                record.getId(), record.getAlarmId(), record.getDeviceId(), record.getAlarmType());
    }
}