package com.example.data.service;

import com.example.data.dto.*;
import com.example.data.dto.*;
import com.example.data.pojo.DeviceData;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceDataService {

    void report(DeviceData data);

    List<DeviceData> historybyid(Long deviceId);


    List<DataRecord> latestHistory(Long deviceId, int limit);

    PageDTO<DeviceRowDTO> devices(int page, int size);

    public List<DeviceData> historyAll(int limit);
    DeviceDetailDTO deviceDetail(Long id);

    List<DataPointDTO> historyMetric(Long deviceId, String metric, int limit);

    List<DataPointDTO> historyRange(Long deviceId, String metric, LocalDateTime from, LocalDateTime to);



}