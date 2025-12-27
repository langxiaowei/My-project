package com.example.device.service.impl;

import com.example.device.dto.DeviceRowDTO;
import com.example.device.dto.PageDTO;
import com.example.device.mapper.DeviceMapper;
import com.example.device.pojo.Device;
import com.example.device.service.DeviceService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;

    public DeviceServiceImpl(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    @Override
    public Long add(Device device, Long userId) {
        device.setStatus(1);
        device.setUserId(userId);
        deviceMapper.insert(device);
        return device.getId();
    }

    @Override
    public List<Device> listByUserId(Long userId) {
        return deviceMapper.selectAll(userId);
    }

    @Override
    public Device findById(Long id, Long userId) {
        Device device = deviceMapper.selectById(id);
        if (device == null || !userId.equals(device.getUserId())) {
            return null;  // 也可以抛自定义异常
        }
        return device;
    }

    @Override
    public void update(Device device, Long userId) {
        // 先校验归属
        Device db = deviceMapper.selectById(device.getId());
        if (db == null || !userId.equals(db.getUserId())) {
            throw new RuntimeException("无权修改该设备");
        }
        device.setUserId(userId);
        deviceMapper.update(device);
    }

    @Override
    public void delete(Long id, Long userId) {
        Device db = deviceMapper.selectById(id);
        if (db == null || !userId.equals(db.getUserId())) {
            throw new RuntimeException("无权删除该设备");
        }
        deviceMapper.deleteById(id);
    }

    @Override
    public void markOnline(Long id) {
        deviceMapper.updateStatusById(id, 1);
    }

    @Override
    public void markOfflineBatch(List<Long> ids) {
        deviceMapper.updateStatusBatch(ids, 0);
    }

    @Override
    public Device detail(Long id) {
        return deviceMapper.selectById(id);
    }

    @Override
    public PageDTO<DeviceRowDTO> list(String keyword, String type, Integer status, Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;
        List<DeviceRowDTO> list = deviceMapper.selectList(keyword, type, status, offset, s);
        long total = deviceMapper.count(keyword, type, status);
        return new PageDTO<>(total, list);
    }
}