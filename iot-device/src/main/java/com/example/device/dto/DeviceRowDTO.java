package com.example.device.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceRowDTO {
    private Long id;
    private String deviceName;
    private String type;
    private Integer status;
    private String model;
    private String location;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}