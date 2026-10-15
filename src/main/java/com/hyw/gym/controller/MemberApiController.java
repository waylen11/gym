package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dto.MemberInfoDto;
import com.hyw.gym.entity.User;
import com.hyw.gym.service.AuthServiceDaoImpl;
import com.hyw.gym.service.MembertypeDaoImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<MemberInfoDto> me() {
        User user = UserHolder.get();
        if (user == null) {
            return Result.error(401, "未登录");
        }
        MemberInfoDto memberInfoDto=new MemberInfoDto();
        BeanUtils.copyProperties(user,memberInfoDto);
        if (memberInfoDto.getSex()==0){
            memberInfoDto.setGender("男");
        }else memberInfoDto.setGender("女");
        return Result.success(memberInfoDto);
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

}
