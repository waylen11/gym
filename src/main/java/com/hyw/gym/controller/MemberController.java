package com.hyw.gym.controller;


import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dao.MemberttypeDao;
import com.hyw.gym.dao.chongzhiDao;
import com.hyw.gym.entity.Chongzhi;
import com.hyw.gym.entity.Membertype;
import com.hyw.gym.entity.User;
import com.hyw.gym.service.MemberDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 会员管理Controller控制层
 */
@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private  chongzhiDao chongzhiDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private MemberDaoImpl memberDaoImpl;
    @Autowired
    private MemberttypeDao memberttypeDao;

    /**
     * @Description: 会员管理-进入会员到期界面
     */
    @RequestMapping("/jin2")
    public String jin2(){
        return "WEB-INF/jsp/HYMemberDaoQi";
    }

    /**
     * @Description: 会员管理-进入会员续费充值界面
     */
    @RequestMapping("/jin3")
    public String jin3(){
        return "WEB-INF/jsp/HYMemberChongZhi";
    }

    /**
     * @Description: 会员管理-进入会员余额充值界面
     */
    @RequestMapping("/jin11")
    public String jin11(){
        return "WEB-INF/jsp/HYMemberyeChongZhi";
    }

    /**
     * @Description: 会员管理-进入会员列表界面
     */
    @RequestMapping("/jin")
    public String jin(){

        return "WEB-INF/jsp/HYMember";
    }

    /**
     * @Description: 教练管理-进入会员私教详情界面
     */
    @RequestMapping("/jin4")
    public String jin4(){

        return "WEB-INF/jsp/privatecoachinfo";
    }

    /**
     * @Description: 会员列表-分页查询
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> query(long ktype,String hyname, int pageSize, int pageNumber){
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("hyname",hyname);
        map1.put("ktype",ktype);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return memberDaoImpl.query(map1);
    }

    /**
     * @Description: 会员到期-分页查询
     */
    @RequestMapping("/query2")
    @ResponseBody
    public Map<String,Object> query2(long ktype,String hyname, int pageSize, int pageNumber){
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("hyname",hyname);
        map1.put("ktype",ktype);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return memberDaoImpl.query2(map1);
    }

    /**
     * @Description: 会员续卡-根据会员id查询会员信息
     */
    @RequestMapping("/cha")
    @ResponseBody
    public User query2(long id){
        return memberDaoImpl.cha(id);
    }

    /**
     * @Description: 会员管理-根据会员id删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,Object> del(long id){
        memberDaoImpl.del(id);
        return query(0,"",5,1);
    }

    /**
     * @Description: 会员管理-添加新会员
     */
    @RequestMapping("/insert")
    @ResponseBody
    public Map<String,Object> insert(User member){
        memberDaoImpl.insert(member);
        return query(0,"",5,1);
    }

    /**
     * @Description: 会员管理-修改会员信息
     */
    @RequestMapping("/update")
    @ResponseBody
    public Map<String,Object> update(User member){
        memberDaoImpl.update(member);
        return query(0,"",5,1);
    }


    /**
     * @Description: 会员卡续费-直接支付购买,累加剩余天数和到期日期
     */
    @RequestMapping("/ins")
    @ResponseBody
    public Map<String, Object> insert(Chongzhi chongzhi, String daoqi){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.sql.Timestamp dat=java.sql.Timestamp.valueOf(df.format(new Date()));

        chongzhi.setRechargeTime(dat);
        chongzhi.setCzStatic(2L);
        chongzhiDao.save(chongzhi);

        Membertype membertype = memberttypeDao.findById(chongzhi.getMembertype().getId()).get();

        User member=userDao.findById(chongzhi.getMember().getId()).get();
        member.setId(chongzhi.getMember().getId());
        member.setStatus(1);
        member.setMemberType(membertype);

        // 1. 累加剩余天数: 根据卡类型的 validDays + 原有 remainingDays
        int addDays = (int)membertype.getValidDays();
        int oldDays = member.getRemainingDays() != null ? member.getRemainingDays() : 0;
        int newDays = oldDays + addDays;
        member.setRemainingDays(newDays);

        // 2. 到期日期:取传入的 daoqi
        java.sql.Date date=java.sql.Date.valueOf(daoqi);
        member.setMemberXufei(date);

        // 3. 根据 remainingDays 自动设置 status(剩余天数>0=正常,否则=请续卡)
        member.setStatus(newDays > 0 ? 1 : 0);
        userDao.save(member);

        return query(0,null,5,1);
    }

}
