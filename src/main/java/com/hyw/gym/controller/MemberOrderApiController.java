package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.OrderDTO;
import com.hyw.gym.service.MemberOrderDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员端-模拟支付接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberOrderApiController {

    @Autowired
    private MemberOrderDaoImpl memberOrderDao;

    // 创建订单
    @PostMapping("/order/create")
    public Result<OrderDTO> create(@RequestParam Long typeId) {
        return memberOrderDao.createOrder(typeId);
    }

    // 模拟支付
    @PostMapping("/order/pay/{orderId}")
    public Result<Map<String, Object>> pay(@PathVariable Long orderId) {
        return memberOrderDao.pay(orderId);
    }

    // 我的订单列表
    @GetMapping("/orders")
    public Result<List<OrderDTO>> orders() {
        return memberOrderDao.myOrders();
    }
}
