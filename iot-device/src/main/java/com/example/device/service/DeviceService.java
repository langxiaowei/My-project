package com.example.device.service;

import com.example.device.dto.DeviceRowDTO;
import com.example.device.pojo.Device;
import com.example.device.dto.PageDTO;
import java.util.List;

public interface DeviceService {

    Long add(Device device, Long userId);

    List<Device> listByUserId(Long userId);

    Device findById(Long id, Long userId);

    void update(Device device, Long userId);

    void delete(Long id, Long userId);

    void markOnline(Long id);
    void markOfflineBatch(List<Long> ids);

    Device detail(Long id);

    PageDTO<DeviceRowDTO> list(String keyword, String type, Integer status, Integer page, Integer size);

}