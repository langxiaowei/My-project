package com.example.user.service.impl;

import com.example.user.dto.UserLoginDTO;
import com.example.user.dto.UserRegisterDTO;
import com.example.user.mapper.UserMapper;
import com.example.user.pojo.User;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Long register(UserRegisterDTO dto) {
        // 1. 检查用户名是否已存在（如果你还没写，建议补一下）

        User user = new User();
        user.setUsername(dto.getUsername());

        // 关键：加密
        String encodedPwd = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPwd);

        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User login(UserLoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 使用 BCrypt 校验明文和密文是否匹配
        boolean match = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!match) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
}