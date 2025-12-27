package com.example.data.dto;

import lombok.Data;

@Data
public class DeviceDetailDTO {
    private Long id;
    private Integer status;
    private String deviceName;
    private String type;
    private String model;
    private String location;

    private String lastReportTime;   // "yyyy-MM-dd HH:mm:ss"
    private Integer todayReports;    // 今日上报次数
}