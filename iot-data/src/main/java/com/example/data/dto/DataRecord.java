package com.example.data.dto;

import lombok.Data;

@Data
public class DataRecord {
    /**
     * 时间字符串，给前端画图做横轴用，比如 16:18:22
     */
    private String time;

    /**
     * 采集值
     */
    private Double value;
}