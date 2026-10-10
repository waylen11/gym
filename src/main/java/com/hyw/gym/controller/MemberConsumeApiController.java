package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.ConsumeRecordDTO;
import com.hyw.gym.service.MemberConsumeDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员端-消费记录接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberConsumeApiController {

    @Autowired
    private MemberConsumeDaoImpl memberConsumeDao;

    // 4.20 我的消费记录
    @GetMapping("/consumptions")
    public Result<List<ConsumeRecordDTO>> consumptions() {
        return memberConsumeDao.showMyConsumptions();
    }
}
