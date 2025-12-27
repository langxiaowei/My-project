package com.example.data.mapper;

import com.example.data.pojo.Alarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AlarmMapper {

    int insert(Alarm alarm);

    List<Alarm> selectList(@Param("deviceId") Long deviceId,
                           @Param("alarmType") String alarmType,
                           @Param("level") Integer level,
                           @Param("status") Integer status,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size);

    long count(@Param("deviceId") Long deviceId,
               @Param("alarmType") String alarmType,
               @Param("level") Integer level,
               @Param("status") Integer status,
               @Param("startTime") LocalDateTime startTime,
               @Param("endTime") LocalDateTime endTime);

    // 新增：更新状态（闭环核心）
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status);

    // 可选：单条详情
    Alarm selectById(@Param("id") Long id);

    int ack(@Param("id") Long id);
    int close(@Param("id") Long id);

    long countToday(@Param("status") Integer status, @Param("level") Integer level);
}