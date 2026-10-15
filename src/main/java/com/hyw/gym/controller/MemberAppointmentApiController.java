package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.dto.AppointmentCreateDTO;
import com.hyw.gym.dto.AppointmentListDTO;
import com.hyw.gym.service.MemberAppointmentDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员端-预约接口
 */
@RestController
@RequestMapping("/api/member")
public class MemberAppointmentApiController {

    @Autowired
    private MemberAppointmentDaoImpl memberAppointmentDao;

    // 创建预约
    @PostMapping("/appointment")
    public Result<Void> create(@RequestBody AppointmentCreateDTO dto) {
        return memberAppointmentDao.createAppointment(dto);
    }

    //查看我的预约
    @GetMapping("/appointments")
    public Result<List<AppointmentListDTO>> appointments(
            @RequestParam(required = false) Integer status) {
        return memberAppointmentDao.showMyAppointments(status);
    }

    // 4.17 取消预约
    @DeleteMapping("/appointment/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        return memberAppointmentDao.cancelAppointment(id);
    }
}
