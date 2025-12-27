package com.example.data.dto;

import lombok.Data;

@Data
public class DeviceRowDTO {
    private Long id;
    private Integer status;          // 1在线 0离线
    private String deviceName;
    private String model;
    private String location;

    private String lastReportTime;   // "yyyy-MM-dd HH:mm:ss"
    private Integer todayReports;    // 今日上报次数
}