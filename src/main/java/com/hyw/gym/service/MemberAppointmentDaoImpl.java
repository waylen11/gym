package com.hyw.gym.service;
// @author 黄元威

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.CourseAppointmentDao;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dto.AppointmentCreateDTO;
import com.hyw.gym.dto.AppointmentListDTO;
import com.hyw.gym.entity.CourseAppointment;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会员端-创建预约
 * Redis 分布式锁防重复提交：SETNX lock:appointment:{memberId}:{date}
 */
@Service
public class MemberAppointmentDaoImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;
    @Autowired
    private CourseAppointmentDaoImpl courseAppointmentDaoImpl;
    @Autowired
    private CourseAppointmentDao courseAppointmentDao;

    private static final String LOCK_PREFIX = "lock:appointment:";
    private static final String LIST_CACHE_PREFIX = "cache:appointments:";
    private static final long LOCK_EXPIRE_SECONDS = 10;

    public Result<Void> createAppointment(AppointmentCreateDTO dto) {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        // 2. 参数 + 日期格式校验
        if (dto.getRecordId() == null || dto.getAppointmentDate() == null) {
            return Result.error(400, "参数不完整");
        }
        java.sql.Date appointmentDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date parsed = sdf.parse(dto.getAppointmentDate());
            appointmentDate = new java.sql.Date(parsed.getTime());
        } catch (Exception e) {
            return Result.error(400, "日期格式错误");
        }
        // 3. 校验私教购买记录存在 + 归属当前会员（防越权）
        PrivateCoachInfo info = privateCoachInfoDao.findById(dto.getRecordId()).orElse(null);
        if (info == null || info.getMember() == null
                || info.getMember().getId() != current.getId()) {
            return Result.error(400, "私教购买记录不存在");
        }
        // 4. Redis 分布式锁：防止同一会员同一天重复提交（双击/并发）
        String lockKey = LOCK_PREFIX + current.getId() + ":" + dto.getAppointmentDate();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1");
        if (!Boolean.TRUE.equals(locked)) {
            return Result.error(400, "请勿重复提交");
        }
        redisTemplate.expire(lockKey, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        try {
            // 5. 组装预约并调用已有的 create()（内含次数校验 + DB 防重复校验）
            CourseAppointment appointment = new CourseAppointment();
            appointment.setRecord(info);
            appointment.setSubject(info.getSubject());
            appointment.setCoach(info.getCoach());
            appointment.setMember(info.getMember());
            appointment.setAppointmentDate(appointmentDate);
            appointment.setRemark(dto.getRemark());
            appointment.setStatus(0);
            appointment.setCreateTime(new Timestamp(System.currentTimeMillis()));
            int code = courseAppointmentDaoImpl.create(appointment);
            // 6. 按返回码映射文档定义的提示文案
            switch (code) {
                case 1:
                    return Result.error(400, "该会员当日已预约此教练，不能重复预约");
                case 2:
                    return Result.error(400, "私教购买记录不存在");
                case 3:
                    return Result.error(400, "剩余次数为0，请先续课");
                case 0:
                    // 预约成功，删除该会员预约列表缓存
                    redisTemplate.delete(LIST_CACHE_PREFIX + current.getId());
                    return Result.success(null);
                default:
                    return Result.error(500, "预约失败，请稍后重试");
            }
        } finally {
            // 7. 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    // 4.16 我的预约列表（status 可选：0已预约 1已打卡 2已取消，不传查全部）
    public Result<List<AppointmentListDTO>> showMyAppointments(Integer status) {
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        Long memberId = current.getId();
        // 1. 查缓存
        String key = LIST_CACHE_PREFIX + memberId;              // cache:appointments:{memberId}
        String cached = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cached)) {
            List<AppointmentListDTO> cachedList = JSONUtil.toList(cached, AppointmentListDTO.class);
            // 缓存里存的是全量，按 status 在内存过滤
            if (status != null) cachedList.removeIf(d -> d.getStatus() != status);
            return Result.success(cachedList);
        }
        // 2. 查库：复用管理端已有的 queryByMember
        List<CourseAppointment> list = courseAppointmentDaoImpl.queryByMember(memberId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AppointmentListDTO> vos = new ArrayList<>();
        for (CourseAppointment a : list) {
            AppointmentListDTO vo = new AppointmentListDTO();
            vo.setAppointmentId(a.getId());
            if (a.getRecord() != null) vo.setRecordId(a.getRecord().getId());
            if (a.getSubject() != null) {
                AppointmentListDTO.SubjectItem si = new AppointmentListDTO.SubjectItem();
                si.setId(a.getSubject().getId());
                si.setCourseName(a.getSubject().getCourseName());
                vo.setSubject(si);
            }
            if (a.getCoach() != null) {
                AppointmentListDTO.CoachItem ci = new AppointmentListDTO.CoachItem();
                ci.setId(a.getCoach().getId());
                ci.setRealName(a.getCoach().getRealName());
                vo.setCoach(ci);
            }
            vo.setAppointmentDate(a.getAppointmentDate() == null ? null : a.getAppointmentDate().toString());
            vo.setStatus(a.getStatus());
            vo.setStatusText(statusText(a.getStatus()));
            vo.setCheckinTime(a.getCheckinTime() == null ? null : sdf.format(a.getCheckinTime()));
            vo.setRemark(a.getRemark());
            vo.setCreateTime(a.getCreateTime() == null ? null : sdf.format(a.getCreateTime()));
            vos.add(vo);
        }
        // 3. 写缓存（3 分钟）
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(vos), 3, TimeUnit.MINUTES);
        // 4. status 过滤（新查出来的也统一在这过滤）
        if (status != null) vos.removeIf(d -> d.getStatus() != status);
        return Result.success(vos);
    }

    // 状态码 → 中文文案
    private String statusText(int status) {
        switch (status) {
            case 0: return "已预约";
            case 1: return "已打卡";
            case 2: return "已取消";
            default: return "未知";
        }
    }

    // 4.17 取消预约
    public Result<Void> cancelAppointment(Long appointmentId) {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        // 2. 查预约 + 防越权（只能取消自己的预约）
        CourseAppointment appointment = courseAppointmentDao.findById(appointmentId).orElse(null);
        if (appointment == null || appointment.getMember() == null
                || appointment.getMember().getId() != current.getId()) {
            return Result.error(400, "预约不存在");
        }
        // 3. 与创建预约【同一把锁】：lock:appointment:{memberId}:{date}
        String lockKey = LOCK_PREFIX + current.getId() + ":" + appointment.getAppointmentDate();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1");
        if (!Boolean.TRUE.equals(locked)) {
            return Result.error(400, "请勿重复提交");
        }
        redisTemplate.expire(lockKey, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        try {
            // 4. 调用管理端已有的 cancel()（内部：不存在→1，非已预约→2，成功置 status=2）
            int code = courseAppointmentDaoImpl.cancel(appointmentId);
            switch (code) {
                case 1:
                    return Result.error(400, "预约不存在");
                case 2:
                    return Result.error(400, "该预约已处理，无法取消");
                case 0:
                    // 5. 取消成功，删预约列表缓存
                    redisTemplate.delete(LIST_CACHE_PREFIX + current.getId());
                    return Result.success(null);
                default:
                    return Result.error(500, "取消失败，请稍后重试");
            }
        } finally {
            // 6. 释放锁
            redisTemplate.delete(lockKey);
        }
    }


}
