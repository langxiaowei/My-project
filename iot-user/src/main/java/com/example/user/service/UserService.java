package com.example.user.service;

import com.example.user.dto.UserLoginDTO;
import com.example.user.dto.UserRegisterDTO;
import com.example.user.pojo.User;

public interface UserService {

    Long register(UserRegisterDTO dto);

    User login(UserLoginDTO dto);

    User findById(Long id);
}