package com.example.device.mapper;

import com.example.device.dto.DeviceRowDTO;
import com.example.device.pojo.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceMapper {

    // 新建设备
    int insert(Device device);

    // 查询全部
    List<Device> selectAll(@Param("userId") Long userId);

    // 按 id 查询
    Device selectById(@Param("id") Long id);

    // 修改设备
    int update(Device device);

    // 删除设备
    int deleteById(@Param("id") Long id);

    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    List<DeviceRowDTO> selectList(@Param("keyword") String keyword,
                                  @Param("type") String type,
                                  @Param("status") Integer status,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);

    long count(@Param("keyword") String keyword,
               @Param("type") String type,
               @Param("status") Integer status);
}