package com.hyw.gym.controller;

import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dao.SubjectDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.Subject;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import com.hyw.gym.service.CoachDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: 会员私教课程Controller控制层
 */
@Controller
@RequestMapping("/private")
public class PrivateCoachController {
   @Autowired
    private UserDao userDao;
   @Autowired
   private CoachDaoImpl coachDaoImpl;
   @Autowired
    private SubjectDao subjectDao;
   @Autowired
   private PrivateCoachInfoDao privateCoachInfoDao;

    /**
     * @Description: 会员私教课程-进入会员私教课程界面
     */
    @RequestMapping("/jin3")
    public String jin3(){

        return "WEB-INF/jsp/privatecoach";
    }

    /**
     * @Description: 会员私教课程-根据私教姓名分页查询
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> query(String coachname, int pageSize, int pageNumber){
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("coachname",coachname);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return coachDaoImpl.query(map1);
    }

    /**
     * @Description: 会员私教课程-查询所有会员信息
     */
    @RequestMapping("/query2")
    @ResponseBody
    public List<User> query2(){
        return userDao.findByUserType(UserType.MEMBER.getType());
    }

    /**
     * @Description: 会员私教课程-查询教练,课程,会员所有信息
     */
    @RequestMapping("/topcoach")
    @ResponseBody
    public Map<String,Object> topcoach(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("coach",userDao.findByUserType(UserType.COACH.getType()));
        map.put("subject",subjectDao.findAll());
        map.put("member",userDao.findByUserType(UserType.MEMBER.getType()));
        return map ;
    }

    /**
     * @Description: 会员私教课程-添加会员私教课程
     */
    @RequestMapping("/add")
    @ResponseBody
    public  void save(PrivateCoachInfo privateCoachInfo){
//       System.out.println(privateCoachInfo.getMember().getMemberId());
        privateCoachInfoDao.save(privateCoachInfo);
    }

    /**
     * @Description: 会员私教课程-根据课程id查询课程信息
     */
    @RequestMapping("/cha")
    @ResponseBody
    public Optional<Subject> one(long id){
        return subjectDao.findById(id);
    }

    /**
     * @Description: 会员私教课程-根据会员id查询会员信息
     */
    @RequestMapping("/cha2")
    @ResponseBody
    public Optional<User> two(long id){
        return userDao.findById(id);
    }

}
