package com.hyw.gym.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyw.gym.common.JwtUtil;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.entity.User;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    // 与 AuthServiceDaoImpl 保持一致,统一使用 Jackson 序列化用户信息
    private final ObjectMapper objectMapper;
    private static final String TOKEN_KEY = "login:token:";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // 2. 去掉 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 3. 验签，失败抛异常
            JwtUtil.parseToken(token);

            // 4. 查 Redis 拿 userJson
            String tokenKey = TOKEN_KEY + token;
            String userJson = redisTemplate.opsForValue().get(tokenKey);
            if (userJson == null) {
                response.setStatus(401);
                return false;
            }

            // 5. 续期
            redisTemplate.expire(tokenKey, 30, TimeUnit.MINUTES);

            // 6. JSON 转 User(Jackson 反序列化,与登录时写入 Redis 的序列化方式一致)
            User user = objectMapper.readValue(userJson, User.class);

            // 7. 存 UserHolder
            UserHolder.save(user);

            return true;

        } catch (Exception e) {
            log.warn("JWT 无效：{}", e.getMessage());
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        UserHolder.remove();
    }
}