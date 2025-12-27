package com.example.user.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 注意：生产环境不要写死在代码里，这里先图省事
    // 至少 32 字节，随便写一段长点的英文 + 数字
    private static final String SECRET = "my-iot-platform-user-jwt-secret-key-1234567890";

    // 生成签名用的 key
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // token 过期时间：例如 2 小时
    private static final long EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    /**
     * 生成 token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expireAt = new Date(now + EXPIRE_MILLIS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expireAt)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，拿到 Claims
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}