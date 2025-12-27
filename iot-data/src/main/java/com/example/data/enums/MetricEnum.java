package com.example.data.enums;

public enum MetricEnum {
    TEMP("temp"),
    HUMI("humi"),
    BATTERY("battery");
    private final String column;

    MetricEnum(String column) {
        this.column = column;
    }
    public String column() {
        return column;
    }

    public static MetricEnum of(String metric) {
        if (metric == null || metric.isBlank()) {
            return TEMP; // 默认温度
        }
        return switch (metric.toLowerCase()) {
            case "temp" -> TEMP;
            case "humi" -> HUMI;
            case "battery" -> BATTERY;
            default -> throw new IllegalArgumentException("Unsupported metric: " + metric);
        };
    }
}