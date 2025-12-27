package com.example.device.controller;

import com.example.device.dto.DeviceRowDTO;
import com.example.device.dto.PageDTO;
import com.example.device.dto.R;
import com.example.device.pojo.Device;
import com.example.device.service.DeviceService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // 新建设备
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Device device,
                                   @RequestHeader("X-User-Id") Long userId) {
        Long id = deviceService.add(device, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "设备创建成功");
        result.put("deviceId", id);
        return result;
    }

    // 当前用户的设备列表
    @GetMapping("/my/list")
    public List<Device> list(@RequestHeader("X-User-Id") Long userId) {

        return deviceService.listByUserId(userId);
    }

    // 当前用户设备详情
    @GetMapping("/info/{id}")
    public Device info(@PathVariable("id") Long id,
                       @RequestHeader("X-User-Id") Long userId) {
        return deviceService.findById(id, userId);
    }

    // 修改设备
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Device device,
                                      @RequestHeader("X-User-Id") Long userId) {
        deviceService.update(device, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "设备更新成功");
        return result;
    }

    // 删除设备
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable("id") Long id,
                                      @RequestHeader("X-User-Id") Long userId) {
        deviceService.delete(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "设备删除成功");
        return result;
    }

    // 标记在线
    @PutMapping("/{id}/online")
    public Map<String, Object> online(@PathVariable("id") Long id) {
        deviceService.markOnline(id);
        return Map.of("message", "已标记在线");
    }

    // 批量标记离线（定时任务用）
    @PutMapping("/offline/batch")
    public Map<String, Object> offlineBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of("message", "无需处理");
        }
        deviceService.markOfflineBatch(ids);
        return Map.of("message", "已批量标记离线", "count", ids.size());
    }
    @GetMapping("/detail")
    public R<Device> detail(@RequestParam("id") Long id) {
        Device d = deviceService.detail(id);
        return d != null ? R.ok(d) : R.fail("not found");
    }

    @GetMapping("/list")
    public PageDTO<DeviceRowDTO> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return deviceService.list(keyword, type, status, page, size);
    }
}