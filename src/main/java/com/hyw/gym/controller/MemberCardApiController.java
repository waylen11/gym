package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.CardInfoVO;
import com.hyw.gym.service.MemberCardDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 会员卡接口：购买/续费会员卡（按卡类型 id 购买）
 */
@RestController
@RequestMapping("/api/member")
public class MemberCardApiController {
    @Autowired
    private MemberCardDaoImpl memberCardDao;

    // 购买/续费会员卡（按卡类型 id，自动计算金额与到期日）
    @PostMapping("/renew/{id}")
    public Result<Map<String, Object>> renew(@PathVariable Long id) {
        return memberCardDao.renew(id);
    }

    @GetMapping("/cardInfo")
    public Result<CardInfoVO> cardInfo(){
        return  memberCardDao.cardInfo();
    }

}
