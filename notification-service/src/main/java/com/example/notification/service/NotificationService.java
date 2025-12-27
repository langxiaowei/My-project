package com.example.notification.service;

import com.example.notification.dto.AlarmEventMsg;

public interface NotificationService {
    void saveNotification(AlarmEventMsg msg);
}