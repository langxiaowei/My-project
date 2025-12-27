package com.example.user.mapper;

import com.example.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insert(User user);

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);
}