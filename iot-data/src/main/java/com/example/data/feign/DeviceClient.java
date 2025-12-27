package com.example.data.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.Map;
@FeignClient(name = "iot-device")
public interface DeviceClient {

    @PutMapping("/{id}/online")
    Map<String, Object> markOnline(@PathVariable("id") Long id);

    @PutMapping("/offline/batch")
    Map<String, Object> markOfflineBatch(@RequestBody List<Long> ids);
}