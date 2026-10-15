package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.CourseAppointment;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dao.SubjectDao;
import com.hyw.gym.service.CourseAppointmentDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 课程预约Controller控制层
 */
@Controller
@RequestMapping("/appointment")
public class CourseAppointmentController {

    @Autowired
    private CourseAppointmentDaoImpl courseAppointmentDaoImpl;
    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SubjectDao subjectDao;

    /**
     * @Description: 管理端-进入预约管理页面
     */
    @RequestMapping("/manage")
    public String manage(Model model) {
        model.addAttribute("members", userDao.findByUserType(UserType.MEMBER.getType()));
        model.addAttribute("coaches", userDao.findByUserType(UserType.COACH.getType()));
        return "WEB-INF/jsp/appointmentManage";
    }

    /**
     * @Description: 管理端-查询所有预约
     */
    @RequestMapping("/queryAll")
    @ResponseBody
    public List<CourseAppointment> queryAll() {
        return courseAppointmentDaoImpl.queryAll();
    }

    /**
     * @Description: 管理端/会员端-创建预约
     * 参数:recordId(私教购买记录), appointmentDate(预约日期), remark(备注)
     */
    @RequestMapping("/create")
    @ResponseBody
    public Map<String, Object> create(Long recordId, String appointmentDate, String remark) {
        Map<String, Object> result = new HashMap<String, Object>();
        CourseAppointment a = new CourseAppointment();
        PrivateCoachInfo info = privateCoachInfoDao.findById(recordId).orElse(null);
        if (info == null) {
            result.put("code", 2);
            result.put("msg", "私教购买记录不存在");
            return result;
        }
        a.setRecord(info);
        a.setSubject(info.getSubject());
        a.setCoach(info.getCoach());
        a.setMember(info.getMember());
        try {
            a.setAppointmentDate(Date.valueOf(appointmentDate));
        } catch (Exception e) {
            result.put("code", 4);
            result.put("msg", "日期格式错误");
            return result;
        }
        a.setRemark(remark);
        int code = courseAppointmentDaoImpl.create(a);
        String[] msgs = {"创建成功", "该会员当日已预约此教练,不能重复预约", "私教购买记录不存在", "剩余次数为0,请先续课"};
        result.put("code", code);
        result.put("msg", msgs[code]);
        return result;
    }

    /**
     * @Description: 教练端-查询自己名下的预约(按日期范围)
     */
    @RequestMapping("/myAppointments")
    @ResponseBody
    public List<CourseAppointment> myAppointments(String startDate, String endDate, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        return courseAppointmentDaoImpl.queryByCoach(user.getId(), start, end);
    }

    /**
     * @Description: 教练端-打卡(扣减次数)
     */
    @RequestMapping("/checkin")
    @ResponseBody
    public Map<String, Object> checkin(Long appointmentId) {
        Map<String, Object> result = new HashMap<String, Object>();
        int code = courseAppointmentDaoImpl.checkin(appointmentId);
        String[] msgs = {"打卡成功", "预约不存在", "该预约已处理(已打卡或已取消)", "该会员剩余次数为0"};
        result.put("code", code);
        result.put("msg", msgs[code]);
        return result;
    }

    /**
     * @Description: 教练端-取消预约(不扣次数)
     */
    @RequestMapping("/cancel")
    @ResponseBody
    public Map<String, Object> cancel(Long appointmentId) {
        Map<String, Object> result = new HashMap<String, Object>();
        int code = courseAppointmentDaoImpl.cancel(appointmentId);
        String[] msgs = {"取消成功", "预约不存在", "该预约已处理,无法取消"};
        result.put("code", code);
        result.put("msg", msgs[code]);
        return result;
    }

    /**
     * @Description: 管理端-根据会员id查询其所有私教购买记录(下拉用)
     */
    @RequestMapping("/memberRecords")
    @ResponseBody
    public List<PrivateCoachInfo> memberRecords(Long memberId) {
        return privateCoachInfoDao.queryByIdNative(memberId);
    }

    private Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Date.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
