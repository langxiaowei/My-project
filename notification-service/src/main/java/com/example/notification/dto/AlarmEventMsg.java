package com.example.notification.dto; // notification 那边改成 com.example.notification.dto

import lombok.Data;

@Data
public class AlarmEventMsg {
    private Long alarmId;     // 告警表主键（如果你有）
    private Long deviceId;    // 设备ID
    private String alarmType; // 告警类型：TEMP_HIGH / HUMI_LOW ...
    private Integer level;    // 级别：1/2/3
    private String content;   // 文本描述
    private Long ts;          // 事件时间戳
}