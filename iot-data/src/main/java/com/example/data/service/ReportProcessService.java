package com.example.data.service;

import com.example.data.dto.DeviceReportMsg;

public interface ReportProcessService {
    void process(DeviceReportMsg msg);
}