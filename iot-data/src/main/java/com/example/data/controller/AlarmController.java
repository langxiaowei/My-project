package com.example.data.controller;

import com.example.data.dto.PageDTO;
import com.example.data.dto.R;
import com.example.data.pojo.Alarm;
import com.example.data.service.AlarmService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class AlarmController {

    private final AlarmService alarmService;

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping("/list")
    public PageDTO<Alarm> list(
            @RequestParam(value = "deviceId", required = false) Long deviceId,
            @RequestParam(value = "alarmType", required = false) String alarmType,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "status", required = false) Integer status,

            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,

            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,

            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        return alarmService.list(deviceId, alarmType, level, status, startTime, endTime, page, size);
    }

    @PostMapping("/ack")
    public R<Boolean> ack(@RequestParam("id") Long id) {
        int n = alarmService.ack(id);
        return n > 0 ? R.ok(true) : R.fail("only NEW(status=0) can ack");
    }

    @PostMapping("/close")
    public R<Boolean> close(@RequestParam("id") Long id) {
        int n = alarmService.close(id);
        return n > 0 ? R.ok(true) : R.fail("only ACKED(status=1) can close");
    }

    @GetMapping("/detail")
    public R detail(@RequestParam("id") Long id) {
        Alarm a = alarmService.detail(id);
        return a != null ? R.ok(a) : R.fail("not found");
    }
}