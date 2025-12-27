package com.example.notification.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationRecord {
    private Long id;
    private Long alarmId;
    private Long deviceId;
    private String alarmType;
    private Integer level;
    private String content;
    private Integer status; // 0/1/2
    private LocalDateTime createTime;
}