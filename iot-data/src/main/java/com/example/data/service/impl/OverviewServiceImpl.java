package com.example.data.service.impl;

import com.example.data.dto.OverviewDTO;
import com.example.data.mapper.AlarmMapper;
import com.example.data.mapper.OverviewMapper;
import com.example.data.service.OverviewService;
import org.springframework.stereotype.Service;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final OverviewMapper overviewMapper;
    private final AlarmMapper alarmMapper;

    public OverviewServiceImpl(OverviewMapper overviewMapper, AlarmMapper alarmMapper) {
        this.overviewMapper = overviewMapper;
        this.alarmMapper = alarmMapper;
    }

    @Override
    public OverviewDTO overview() {
        OverviewDTO dto = new OverviewDTO();

        dto.setTotalDevices(overviewMapper.totalDevices());
        dto.setOnlineDevices(overviewMapper.onlineDevices());
        dto.setTodayReports(overviewMapper.todayReports());
        dto.setLastReportTime(overviewMapper.lastReportTime());

        dto.setTodayAlarmCount(alarmMapper.countToday(null, null));
        dto.setTodayNewAlarmCount(alarmMapper.countToday(0, null));
        dto.setTodayCriticalAlarmCount(alarmMapper.countToday(null, 3));

        return dto;
    }
}