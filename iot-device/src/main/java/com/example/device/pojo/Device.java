package com.example.device.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Device {
    private Long id;
    private String deviceName;
    private String type;        // sensor / actuator ...
    private Integer status;     // 0离线 1在线（先按这个）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long userId;
    private String model;
    private String location;
}