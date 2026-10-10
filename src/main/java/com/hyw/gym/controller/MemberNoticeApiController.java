package com.hyw.gym.controller;

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.MessageDTO;
import com.hyw.gym.entity.Messages;
import com.hyw.gym.service.MemberNoticeDaoimpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberNoticeApiController {
    @Autowired
    private MemberNoticeDaoimpl memberNoticeDaoimpl;

    @GetMapping("/notices")
    public Result<List<MessageDTO>> showNotices(){
        return memberNoticeDaoimpl.showNotices();
    }
}
