package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.GoodsDTO;
import com.hyw.gym.service.MemberGoodsDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员端-商品列表接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberGoodsApiController {

    @Autowired
    private MemberGoodsDaoImpl memberGoodsDao;

    // 在售商品列表（含售罄标注）
    @GetMapping("/goods")
    public Result<List<GoodsDTO>> goods() {
        return memberGoodsDao.goods();
    }
}
