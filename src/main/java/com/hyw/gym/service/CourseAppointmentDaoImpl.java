package com.hyw.gym.service;

import com.hyw.gym.dao.CourseAppointmentDao;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.entity.CourseAppointment;
import com.hyw.gym.entity.PrivateCoachInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Description: 课程预约Service实现层
 */
@Service
public class CourseAppointmentDaoImpl {

    @Autowired
    private CourseAppointmentDao courseAppointmentDao;

    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;

    /**
     * 创建预约 - 会员端/管理员调用
     * 返回: 0=成功 1=该会员当日已预约此教练 2=购买记录不存在 3=次数已用完
     */
    public int create(CourseAppointment appointment) {
        Long recordId = appointment.getRecord() != null ? appointment.getRecord().getId() : null;
        if (recordId == null) {
            return 2;
        }
        PrivateCoachInfo info = privateCoachInfoDao.findById(recordId).orElse(null);
        if (info == null) {
            return 2;
        }
        if (info.getCount() <= 0) {
            return 3;
        }
        // 同一天同一会员同一教练不能重复预约
        List<CourseAppointment> exists = courseAppointmentDao.findActiveByMemberCoachDate(
                appointment.getMember().getId(),
                appointment.getCoach().getId(),
                appointment.getAppointmentDate()
        );
        if (exists != null && !exists.isEmpty()) {
            return 1;
        }
        appointment.setStatus(0);
        appointment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        courseAppointmentDao.save(appointment);
        return 0;
    }

    /**
     * 教练打卡 - 把预约状态改为已打卡,同时扣减购买记录的剩余次数
     * 返回: 0=成功 1=预约不存在 2=状态非已预约 3=剩余次数为0
     */
    public int checkin(Long appointmentId) {
        CourseAppointment a = courseAppointmentDao.findById(appointmentId).orElse(null);
        if (a == null) {
            return 1;
        }
        if (a.getStatus() != 0) {
            return 2;
        }
        PrivateCoachInfo info = a.getRecord();
        if (info == null || info.getCount() <= 0) {
            return 3;
        }
        a.setStatus(1);
        a.setCheckinTime(new Timestamp(System.currentTimeMillis()));
        courseAppointmentDao.save(a);

        info.setCount(info.getCount() - 1);
        privateCoachInfoDao.save(info);
        return 0;
    }

    /**
     * 教练取消预约 - 不扣减次数
     * 返回: 0=成功 1=预约不存在 2=状态非已预约
     */
    public int cancel(Long appointmentId) {
        CourseAppointment a = courseAppointmentDao.findById(appointmentId).orElse(null);
        if (a == null) {
            return 1;
        }
        if (a.getStatus() != 0) {
            return 2;
        }
        a.setStatus(2);
        courseAppointmentDao.save(a);
        return 0;
    }

    /**
     * 查询教练名下预约(按日期范围)
     */
    public List<CourseAppointment> queryByCoach(Long coachId, Date startDate, Date endDate) {
        return courseAppointmentDao.queryByCoach(coachId, startDate, endDate);
    }

    /**
     * 查询会员名下所有预约
     */
    public List<CourseAppointment> queryByMember(Long memberId) {
        return courseAppointmentDao.queryByMember(memberId);
    }

    /**
     * 查询所有预约(管理员)
     */
    public List<CourseAppointment> queryAll() {
        return courseAppointmentDao.queryAll();
    }
}
