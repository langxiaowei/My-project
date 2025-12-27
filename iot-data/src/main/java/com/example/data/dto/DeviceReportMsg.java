package com.example.data.dto;

import lombok.Data;

@Data
public class DeviceReportMsg {
    private String msgId;     // 幂等关键
    private Long deviceId;
    private Double temp;
    private Double humi;
    private Integer battery;

    private String ip;
    private Long ts;          // 客户端/服务端时间戳都行
}