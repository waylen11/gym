package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.entity.User;
import com.hyw.gym.service.AuthServiceDaoImpl;
import com.hyw.gym.service.MembertypeDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberApiController {
    @Autowired
    private AuthServiceDaoImpl authServiceDaoImpl;
    @Autowired
    private MembertypeDaoImpl membertypeDao;
    // 获取验证码
    @PostMapping("/code")
    public Result<String> sendcode(@RequestParam String phone) {
        return authServiceDaoImpl.sendcode(phone);
    }
    // 登录
    @PostMapping("/login")
    public Result<String> login(@RequestParam String phone, @RequestParam String code) {
        return authServiceDaoImpl.login(phone, code);
    }
    // 更新用户信息
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        authServiceDaoImpl.update(user);
        return Result.success("更新成功");
    }
    // 获取当前用户信息
    @GetMapping("/me")
    public Result<User> me() {
        User user = UserHolder.get();
        if (user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }
    // 退出登录
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authServiceDaoImpl.logout(token);
        return Result.success();
    }
    // 查询所有会员卡类型
    @GetMapping("/cardType")
    public Result<List> showCardType(){
        List list = membertypeDao.cha();
        return Result.success(list);
    }
    // 购买会员卡
    @PostMapping("/renew")
    public Result<String> renew() {

        return Result.success("购买成功");
    }
}
