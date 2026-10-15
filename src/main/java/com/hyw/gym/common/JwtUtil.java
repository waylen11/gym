package com.hyw.gym.common;
// @author 黄元威

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 密钥
    private static final String SECRET = "hyw-gym-secret-key-2026";

    // 过期时间：30 分钟
    //private static final long EXPIRE = 30 * 60 * 1000L;
    //测试专用7天
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 JWT
     */
    public static String createToken(Long userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 解析 JWT，验签 + 检查过期
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 JWT 取用户 ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 判断 JWT 是否有效
     */
    public static boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}