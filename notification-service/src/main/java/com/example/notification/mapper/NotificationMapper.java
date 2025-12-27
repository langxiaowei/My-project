package com.example.notification.mapper;

import com.example.notification.pojo.NotificationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {
    int insert(NotificationRecord record);
}