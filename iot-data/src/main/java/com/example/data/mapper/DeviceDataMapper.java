package com.example.data.mapper;
import com.example.data.dto.DataPointDTO;
import com.example.data.dto.DataRecord;
import com.example.data.dto.DeviceDetailDTO;
import com.example.data.dto.DeviceRowDTO;
import com.example.data.pojo.DeviceData;
import com.example.data.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DeviceDataMapper {

    int insert(DeviceData data);

    List<DeviceData> selecthistoryByDeviceId(@Param("deviceId") Long deviceId);


    List<DataRecord> selectLatestHistory(@Param("deviceId") Long deviceId,
                                         @Param("limit") int limit);

    List<Long> selectTimeoutOnlineDeviceIds(@Param("cutoffTime") LocalDateTime cutoffTime);

    List<DeviceRowDTO> selectDevicePage(@Param("offset") int offset,
                                        @Param("size") int size);

    Long countDevices();

    List<DeviceData> selectAllHistory(@Param("limit") int limit);

    DeviceDetailDTO selectDeviceDetail(@Param("id") Long id);

    List<DataPointDTO> selectHistoryPoints(
            @Param("deviceId") Long deviceId,
            @Param("metricCol") String metricCol,
            @Param("limit") int limit
    );

    List<DataPointDTO> selectHistoryPointsRange(
            @Param("deviceId") Long deviceId,
            @Param("metricCol") String metricCol,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );



}