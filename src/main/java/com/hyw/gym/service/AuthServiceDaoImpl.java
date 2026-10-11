package com.hyw.gym.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyw.gym.common.JwtUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceDaoImpl {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private HttpServletRequest request;
    /**
     * 使用 Jackson 而非 Hutool 序列化 User:
     * Jackson 能识别实体上的 @JsonIgnore,可截断 User<->Membertype 双向关联导致的循环递归
     */
    @Autowired
    private ObjectMapper objectMapper;

    private static final String CODE_KEY = "login:code:";
    private static final String LIMIT_KEY = "login:code:limit:";
    private static final String TOKEN_KEY = "login:token:";

    public Result sendcode(String phone) {

        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error(400, "手机号格式错误");
        }

        // 防刷①：每日发送上限（10 次/手机号/天），INCR 原子计数
        String dayKey = LIMIT_KEY + "day:" + phone + ":" + LocalDate.now();
        Long dayCount = redisTemplate.opsForValue().increment(dayKey, 1);
        if (dayCount != null && dayCount == 1) {
            redisTemplate.expire(dayKey, 24, TimeUnit.HOURS);   // 第一次计数时设 TTL
        }
        if (dayCount != null && dayCount > 10) {
            return Result.error(400, "今日发送验证码次数已达上限");
        }

        // 防刷②：60 秒内同一手机号只能发一次（SETNX 原子，防连点）
        String intervalKey = LIMIT_KEY + "interval:" + phone;
        Boolean first = redisTemplate.opsForValue().setIfAbsent(intervalKey, "1");
        if (!Boolean.TRUE.equals(first)) {
            return Result.error(400, "发送太频繁，请 60 秒后再试");
        }
        redisTemplate.expire(intervalKey, 60, TimeUnit.SECONDS);

        String code = RandomUtil.randomNumbers(6);

        redisTemplate.opsForValue()
                .set(CODE_KEY + phone, code, 2, TimeUnit.MINUTES);

        log.info("【模拟短信】手机号 {} 验证码：{}", phone, code);

        return Result.success();
    }

    public Result<String> login(String phone, String code) {
        // 校验手机号格式
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error(400, "手机号格式错误");
        }
        // 校验验证码
        String codeKey = CODE_KEY + phone;
        String cachedCode = redisTemplate.opsForValue().get(codeKey);
        if (cachedCode == null || !cachedCode.equals(code)) {
            return Result.error(400, "验证码错误或已过期");
        }
        // 校验用户是否存在
        User user = userDao.findByPhone(phone);
        if (user == null) {
            user=CreatUser(phone);
            log.info("新用户注册：{}，id：{}", phone, user.getId());
        }
        // 登录成功
        String token = JwtUtil.createToken(user.getId(), phone);
        // 缓存 token 到 Redis：7 天，与拦截器续期保持一致（原 30 分钟会导致 30 分钟不活跃就 401）
        redisTemplate.opsForValue()
                .set(TOKEN_KEY + token, String.valueOf(user.getId()), 7 * 24, TimeUnit.HOURS);
        // 验证码一次性：登录成功即删除，防止同一验证码被重复使用
        redisTemplate.delete(codeKey);

        return Result.success(token);
    }

    private User CreatUser(String phone) {
        User user=new User();
        user.setPhone(phone);
        user.setUserType(2);
        user.setLoginName("新用户"+RandomUtil.randomString(6));
        String rawPassword = UUID.randomUUID().toString().replace("-", "");
        user.setPassword(DigestUtils.md5DigestAsHex(rawPassword.getBytes()));
        user=userDao.save(user);
        return user;
    }

    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public void update(User user) {
        // 1. 取当前登录用户
        User currentUser = UserHolder.get();
        if (currentUser == null) {
            throw new RuntimeException("未登录");
        }

        // 2. 从数据库查完整数据
        User existing = userDao.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 3. 密码判空，有才加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }

        // 4. 只拷贝非 null 字段
        BeanUtil.copyProperties(user, existing, CopyOptions.create().setIgnoreNullValue(true));

        // 5. 保存数据库
        userDao.save(existing);

        // 6. 更新 ThreadLocal
        UserHolder.save(existing);

        // 7. 同步更新 Redis
      /*  String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && !token.isEmpty()) {
            String tokenKey = "login:token:" + token;
            Long ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                redisTemplate.opsForValue().set(
                        tokenKey,
                        toJson(existing),
                        ttl, TimeUnit.SECONDS);
            }
        }*/
    }

    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        // 去掉 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 删 Redis
        redisTemplate.delete(TOKEN_KEY + token);
    }



    /**
     * 统一用 Jackson 序列化用户信息,避免 Hutool 无视 @JsonIgnore 导致的双向关联栈溢出
     */
   /* private String toJson(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error("用户信息序列化失败, userId={}", user.getId(), e);
            throw new RuntimeException("登录信息处理失败", e);
        }
    }*/
}
