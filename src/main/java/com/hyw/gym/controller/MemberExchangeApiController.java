package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.ExchangeRecordDTO;
import com.hyw.gym.service.MemberExchangeDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员端-积分兑换商品接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberExchangeApiController {

    @Autowired
    private MemberExchangeDaoImpl memberExchangeDao;

    // 积分兑换商品：POST /api/member/exchange/{goodsId}
    @PostMapping("/exchange/{goodsId}")
    public Result<Map<String, Object>> exchange(@PathVariable Long goodsId) {
        return memberExchangeDao.exchange(goodsId);
    }

    // 我的兑换记录：GET /api/member/exchanges
    @GetMapping("/exchanges")
    public Result<List<ExchangeRecordDTO>> exchanges() {
        return memberExchangeDao.myExchanges();
    }
}
