package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.SignRankDTO;
import com.hyw.gym.dto.SignResultDTO;
import com.hyw.gym.dto.SignStatusDTO;
import com.hyw.gym.service.MemberSignDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会员端-每日签到接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberSignApiController {

    @Autowired
    private MemberSignDaoImpl memberSignDao;

    // 每日签到（带坐标，距离限制）
    @PostMapping("/sign")
    public Result<SignResultDTO> sign(@RequestParam double latitude,
                                      @RequestParam double longitude) {
        return memberSignDao.sign(latitude, longitude);
    }

    @GetMapping("/sign/status")
    public Result<SignStatusDTO> status() {
        return memberSignDao.status();
    }

    // 签到排行榜：本月 Top10 + 我的排名
    @GetMapping("/sign/rank")
    public Result<SignRankDTO> rank() {
        return memberSignDao.rank();
    }
}
