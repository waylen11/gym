package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.LostPropertyDTO;
import com.hyw.gym.service.MemberLostDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员端-失物招领接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberLostApiController {

    @Autowired
    private MemberLostDaoImpl memberLostDao;

    // 失物招领列表（status 可选：0 未认领 / 1 已认领 / 不传=全部，默认未认领在前）
    @GetMapping("/losts")
    public Result<List<LostPropertyDTO>> losts(@RequestParam(required = false) Integer status) {
        return memberLostDao.losts(status);
    }
}
