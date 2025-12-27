package com.example.data.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Alarm {
    private Long id;

    private Long deviceId;

    private String alarmType;   // TEMP_HIGH / HUMI_HIGH / BATTERY_LOW
    private Integer level;      // 1/2/3
    private String metric;      // temp/humi/battery

    private Double threshold;
    private Double actualValue;

    private Integer status;     // 0/1/2
    private String message;

    private LocalDateTime createTime;
}