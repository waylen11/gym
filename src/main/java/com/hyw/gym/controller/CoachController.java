package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import com.hyw.gym.service.CoachDaoImpl;
import com.hyw.gym.service.PrivateCoachInfoDaoImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 教练管理Controller控制层
 */
@Controller
@RequestMapping("/coach")
public class CoachController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;
    @Autowired
    private CoachDaoImpl coachDaoImpl;
    @Autowired
    private PrivateCoachInfoDaoImpl privateCoachInfoDaoImpl;
    @Value("${custom.default.password}")
    private String defaultPassword;

    /**
     * @Description: 教练管理-进入教练列表界面
     */
    @RequestMapping("/jin3")
    public String jin3(){

        return "WEB-INF/jsp/coach";
    }

    /**
     * @Description: 教练管理-发布课程
     */
    @RequestMapping("/jin4")
    public String jin4(){

        return "WEB-INF/jsp/publishsubject";
    }

    /**
     * @Description: 教练管理-根据教练姓名分页查询
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
     * @Description: 教练管理-根据教练id删除教练信息
     */
    @RequestMapping("/del")
    @ResponseBody
    public  Map<String,Object> del(long id,String coachname, int pageSize, int pageNumber){

        //先根据教练id在私教信息表里查询是否有其信息
        List<PrivateCoachInfo> privateCoachInfoList = privateCoachInfoDao.queryByCoachIdNative(id);
        if(privateCoachInfoList !=null && privateCoachInfoList.size() > 0){
            //如果有,先循环删除
            for(PrivateCoachInfo privateCoachInfo : privateCoachInfoList){
                if(id == privateCoachInfo.getCoach().getId()){
                    privateCoachInfoDao.delete(privateCoachInfo);
                }
            }
        }
        userDao.deleteById(id);
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("coachname",coachname);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return coachDaoImpl.query(map1);
    }

    /**
     * @Description: 教练管理-根据教练姓名计算总数据数量
     */
    @RequestMapping("/count")
    @ResponseBody
    public Long count (String coachName){
        coachDaoImpl.count(coachName);
        return  coachDaoImpl.count(coachName);
    }

    /**
     * @Description: 教练管理-添加新教练
     */
    @RequestMapping("/add")
    @ResponseBody
    public void save(User coach){
        coach.setPassword(DigestUtils.md5Hex(defaultPassword));
        coach.setUserType(UserType.COACH.getType());
        coach.setCoachStatic(0);
        userDao.save(coach);
    }

    /**
     * @Description: 教练管理-根据教练id查询
     */
    @RequestMapping("/cha")
    @ResponseBody
    public Optional<User> one(long id){
        return userDao.findById(id);
    }

    /**
     * @Description: 教练管理-修改教练信息
     */
    @RequestMapping("/upd")
    @ResponseBody
    public void upd(User coach){
        Optional<User> user = userDao.findById(coach.getId());
        if(user.isPresent() && UserType.COACH.getType().equals(user.get().getUserType())){
            User one = user.get();
            coach.setLoginName(one.getLoginName());
            coach.setPassword(one.getPassword());
            coach.setUserType(one.getUserType());
            userDao.save(coach);
        }
    }

    /**
     * @Description: 教练端-进入我的会员页面
     */
    @RequestMapping("/myMembers")
    public String myMembers(){
        return "WEB-INF/jsp/myMembers";
    }

    /**
     * @Description: 教练端-查询当前登录教练的所有私教会员
     */
    @RequestMapping("/myMembersQuery")
    @ResponseBody
    public Map<String,Object> myMembersQuery(int pageSize, int pageNumber, HttpSession session){
        User user = (User) session.getAttribute("user");
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("coachid", user.getId());
        map1.put("qi", (pageNumber - 1) * pageSize);
        map1.put("shi", pageSize);
        return privateCoachInfoDaoImpl.query(map1);
    }

    /**
     * @Description: 教练端-进入我的课程安排页面
     */
    @RequestMapping("/mySchedule")
    public String mySchedule(){
        return "WEB-INF/jsp/mySchedule";
    }

    /**
     * @Description: 教练端-查询当前登录教练的课程安排(按日期筛选)
     */
    @RequestMapping("/myScheduleQuery")
    @ResponseBody
    public Map<String,Object> myScheduleQuery(String startDate, String endDate,
                                               int pageSize, int pageNumber, HttpSession session){
        User user = (User) session.getAttribute("user");
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("coachid", user.getId());
        map1.put("qi", (pageNumber - 1) * pageSize);
        map1.put("shi", pageSize);
        Map<String,Object> result = privateCoachInfoDaoImpl.query(map1);
        // 按日期筛选
        List<PrivateCoachInfo> rows = (List<PrivateCoachInfo>) result.get("rows");
        List<PrivateCoachInfo> filtered = new ArrayList<PrivateCoachInfo>();
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = startDate != null && !startDate.equals("") ? sdf.parse(startDate) : null;
            Date end = endDate != null && !endDate.equals("") ? sdf.parse(endDate + " 23:59:59") : null;
            for(PrivateCoachInfo p : rows){
                if(p.getCourseDate() == null){
                    filtered.add(p);
                    continue;
                }
                Date cd = new Date(p.getCourseDate().getTime());
                if(start != null && cd.before(start)) continue;
                if(end != null && cd.after(end)) continue;
                filtered.add(p);
            }
        }catch(Exception e){
            filtered = rows;
        }
        result.put("rows", filtered);
        result.put("total", (long) filtered.size());
        return result;
    }

    /**
     * @Description: 教练端-会员上课打卡(扣减次数)
     */
    @RequestMapping("/checkin")
    @ResponseBody
    public Map<String,Object> checkin(long recordId){
        Map<String,Object> result = new HashMap<String,Object>();
        Optional<PrivateCoachInfo> opt = privateCoachInfoDao.findById(recordId);
        if(!opt.isPresent()){
            result.put("code", 404);
            result.put("msg", "私教记录不存在");
            return result;
        }
        PrivateCoachInfo info = opt.get();
        if(info.getCount() <= 0){
            result.put("code", 400);
            result.put("msg", "该课程剩余次数为0,请续课");
            return result;
        }
        info.setCount(info.getCount() - 1);
        // 记录打卡信息到remark,只保留最近5条打卡记录避免字段超长
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String log = "[打卡]" + sdf.format(new Date()) + ";";
        String oldRemark = info.getRemark() == null ? "" : info.getRemark();
        String[] parts = oldRemark.split(";");
        StringBuilder newRemark = new StringBuilder();
        int start = Math.max(0, parts.length - 4);
        for(int i = start; i < parts.length; i++){
            if(!parts[i].trim().isEmpty()){
                newRemark.append(parts[i].trim()).append(";");
            }
        }
        newRemark.append(log);
        info.setRemark(newRemark.toString());
        privateCoachInfoDao.save(info);
        result.put("code", 200);
        result.put("msg", "打卡成功,剩余次数:" + info.getCount());
        return result;
    }
}
