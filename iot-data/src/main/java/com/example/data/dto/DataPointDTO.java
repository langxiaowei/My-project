package com.example.data.dto;

import lombok.Data;

@Data
public class DataPointDTO {
    private String time;      // "yyyy-MM-dd HH:mm:ss"
    private Double value;     // 你想画的字段，比如温度/电压/whatever
}