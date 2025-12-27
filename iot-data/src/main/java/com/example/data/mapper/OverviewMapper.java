package com.example.data.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OverviewMapper {
    long totalDevices();
    long onlineDevices();
    long todayReports();
    String lastReportTime();
}