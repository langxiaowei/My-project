package com.example.data.service.impl;
import com.example.data.dto.AlarmEventMsg;
import com.example.data.dto.PageDTO;
import com.example.data.mapper.AlarmMapper;
import com.example.data.mq.AlarmEventProducer;
import com.example.data.pojo.Alarm;
import com.example.data.pojo.DeviceData;
import com.example.data.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class AlarmServiceImpl implements AlarmService {
    private final AlarmMapper alarmMapper;
    private final AlarmDedupRedis alarmDedupRedis;
    private final AlarmEventProducer alarmEventProducer;
    @Value("${iot.alarm.temp-high}")
    private double tempHigh;
    @Value("${iot.alarm.humi-high}")
    private double humiHigh;
    @Value("${iot.alarm.battery-low}")
    private double batteryLow;
    public AlarmServiceImpl(AlarmMapper alarmMapper,
                            AlarmDedupRedis alarmDedupRedis,
                            AlarmEventProducer alarmEventProducer) {
        this.alarmMapper = alarmMapper;
        this.alarmDedupRedis = alarmDedupRedis;
        this.alarmEventProducer = alarmEventProducer;
    }
    @Override
    public void checkAndCreateAlarms(DeviceData data) {
        if (data == null || data.getDeviceId() == null) return;

        // 你表里有 temp/humi/battery 就用；没有就先不触发对应告警
        // TEMP_HIGH
        if (data.getTemp() != null && data.getTemp() >= tempHigh) {
            createIfAllowed(data.getDeviceId(), "TEMP_HIGH", 2, "temp", tempHigh, data.getTemp(),
                    "温度超阈值");
        }

        // HUMI_HIGH
        if (data.getHumi() != null && data.getHumi() >= humiHigh) {
            createIfAllowed(data.getDeviceId(), "HUMI_HIGH", 2, "humi", humiHigh, data.getHumi(),
                    "湿度超阈值");
        }

        // BATTERY_LOW
        if (data.getBattery() != null && data.getBattery() <= batteryLow) {
            createIfAllowed(data.getDeviceId(), "BATTERY_LOW", 3, "battery", batteryLow, data.getBattery(),
                    "电量过低");
        }
    }

    private void createIfAllowed(Long deviceId, String alarmType, int level,
                                 String metric, double threshold, double actual, String msg) {
        boolean allow = alarmDedupRedis.allow(deviceId, alarmType);
        if (!allow) {
            log.info("[alarm] dedup hit, deviceId={}, alarmType={}", deviceId, alarmType);
            return;
        }

        Alarm alarm = new Alarm();
        alarm.setDeviceId(deviceId);
        alarm.setAlarmType(alarmType);
        alarm.setLevel(level);
        alarm.setMetric(metric);
        alarm.setThreshold(threshold);
        alarm.setActualValue(actual);
        alarm.setStatus(0);
        alarm.setMessage(msg);
        alarmMapper.insert(alarm);
        // ====== ② 发送告警事件（新增）======
        AlarmEventMsg event = new AlarmEventMsg();
        event.setAlarmId(alarm.getId());          // 注意：insert 后 id 才有值
        event.setDeviceId(alarm.getDeviceId());
        event.setAlarmType(alarm.getAlarmType());
        event.setLevel(alarm.getLevel());
        event.setContent(alarm.getMessage());
        event.setTs(System.currentTimeMillis());

        alarmEventProducer.send(event);
        log.info("[alarm] created, deviceId={}, type={}, actual={}, threshold={}",
                deviceId, alarmType, actual, threshold);
    }

    @Override
    public PageDTO<Alarm> list(Long deviceId, String alarmType, Integer level, Integer status,
                               LocalDateTime startTime, LocalDateTime endTime,
                               Integer page, Integer size) {

        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;

        List<Alarm> list = alarmMapper.selectList(deviceId, alarmType, level, status, startTime, endTime, offset, s);
        long total = alarmMapper.count(deviceId, alarmType, level, status, startTime, endTime);

        return new PageDTO<>(total, list);
    }

    public int ack(Long id){
        return alarmMapper.ack(id);   // 第二次会返回 0
    }
    public int close(Long id){
        return alarmMapper.close(id); // 未ACK直接close会返回0
    }

    @Override
    public Alarm detail(Long id) {
        if (id == null) return null;
        return alarmMapper.selectById(id);
    }
}