package com.example.data.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceData {
    private Long id;
    private Long deviceId;
    private String value;
    private LocalDateTime createTime;
    private Double temp;
    private Double humi;
    private Integer battery;
}