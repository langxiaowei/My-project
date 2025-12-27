package com.example.data.dto;

import lombok.Data;

@Data
public class OverviewDTO {

    /** 设备总数 */
    private Long totalDevices;

    /** 在线设备数（status = 1） */
    private Long onlineDevices;

    /** 今天上报数据的条数 */
    private Long todayReports;

    /** 最近一条上报时间 */
    private String lastReportTime;

    /** 今日告警数 */
    private Long todayAlarmCount;
    /** 今日未确认告警数（status=0） */
    private Long todayNewAlarmCount;
    /** 今日严重告警数（level=3） */
    private Long todayCriticalAlarmCount;

}