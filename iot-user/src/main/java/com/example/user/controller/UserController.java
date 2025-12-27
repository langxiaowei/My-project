package com.example.user.controller;

import com.example.user.dto.UserLoginDTO;
import com.example.user.dto.UserRegisterDTO;
import com.example.user.pojo.User;
import com.example.user.service.UserService;
import com.example.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 原来的测试接口
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "hello from user-service");
        map.put("service", "iot-user");
        return map;
    }

    // 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody @Valid UserRegisterDTO dto) {
        Long id = userService.register(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", id);
        result.put("message", "注册成功");
        return result;
    }

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody @Valid UserLoginDTO dto) {
        User user = userService.login(dto);

        // 生成 token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "登录成功");
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("token", token);
        return result;
    }

    // 按 id 查询
    @GetMapping("/info/{id}")
    public User info(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("message", "未登录");
            return result;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "
        try {
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            result.put("message", "已登录");
            result.put("userId", userId);
            result.put("username", username);
            return result;
        } catch (Exception e) {
            result.put("message", "token 无效或已过期");
            return result;
        }
    }
}