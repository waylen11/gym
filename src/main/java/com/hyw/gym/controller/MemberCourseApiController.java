package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dto.CoachDetailDto;
import com.hyw.gym.dto.CoachListDto;
import com.hyw.gym.dto.MyPrivateCoachDto;
import com.hyw.gym.dto.SubjectListDto;
import com.hyw.gym.entity.Subject;
import com.hyw.gym.entity.User;
import com.hyw.gym.service.MemberCoachDaoimpl;
import com.hyw.gym.service.MemberCourseDaoimpl;
import com.hyw.gym.service.MemberPrivateCourseDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberCourseApiController {
    @Autowired
    private MemberCourseDaoimpl memberCourseDaoimpl;

    @Autowired
    private MemberCoachDaoimpl memberCoachDaoimpl;

    @Autowired
    private MemberPrivateCourseDaoImpl memberPrivateCourseDao;
    //所有课程
    @GetMapping("/courses")
    public Result<List<SubjectListDto>> showCourse(){
        return memberCourseDaoimpl.showCourse();
    }

    //课程详情
    @GetMapping("/course/{id}")
    public Result<Subject> SubjectDetail(@PathVariable Long id){
        return memberCourseDaoimpl.showDetail(id);
    }

    //教练列表
    @GetMapping("/coachlist")
    public Result<List<CoachListDto>> showCoach(){
        return memberCoachDaoimpl.showCoach();
    }
    //教练详情
    @GetMapping("/coach/{id}")
    public Result<CoachDetailDto> coachDetail(@PathVariable Long id) {
        return memberCoachDaoimpl.showCoachDetail(id);
    }

    //我的私教课
    @GetMapping("/privateCourses")
    public Result<List<MyPrivateCoachDto>> MyPrivateCoach(){
        return memberPrivateCourseDao.showMyPrivateCoach();
    }
}
