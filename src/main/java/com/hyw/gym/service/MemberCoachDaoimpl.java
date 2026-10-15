package com.hyw.gym.service;
// @author 黄元威

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dto.CoachDetailDto;
import com.hyw.gym.dto.CoachListDto;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.Subject;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MemberCoachDaoimpl {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;

    private static final String COACH_LIST_KEY = "coach:list";
    private static final String COACH_DETAIL_PREFIX = "coach:detail:";
    private static final long CACHE_EXPIRE = 30;   // 30 分钟

    public Result<List<CoachListDto>> showCoach() {
        String cached = redisTemplate.opsForValue().get(COACH_LIST_KEY);
        if (StrUtil.isNotBlank(cached)) {
            return Result.success(JSONUtil.toList(cached, CoachListDto.class));
        }
        List<User> coaches = userDao.findActiveCoaches();
        List<CoachListDto> coachListDtos = new ArrayList<>();
        for (User c : coaches) {
            CoachListDto coachListDto = new CoachListDto();
            coachListDto.setRealName(c.getRealName());
            coachListDto.setId(c.getId());
            coachListDto.setSex(c.getSex());
            coachListDto.setAge(c.getAge());
            coachListDto.setCoachTeach(c.getCoachTeach());
            coachListDto.setCoachDate(c.getCoachDate());
            coachListDtos.add(coachListDto);
        }
        redisTemplate.opsForValue().set(COACH_LIST_KEY,
                JSONUtil.toJsonStr(coachListDtos), CACHE_EXPIRE, TimeUnit.MINUTES);
        return Result.success(coachListDtos);
    }

    // 教练详情：基本信息 + 名下私教会员数 + 教过的课程
    public Result<CoachDetailDto> showCoachDetail(Long id) {
        String key = COACH_DETAIL_PREFIX + id;
        String cached = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cached)) {
            return Result.success(JSONUtil.toBean(cached, CoachDetailDto.class));
        }
        User coach = userDao.findById(id).orElse(null);
        if (coach == null || !UserType.COACH.getType().equals(coach.getUserType())) {
            return Result.error(400, "教练不存在");
        }
        // 该教练的所有私教记录 → 课程去重 + 会员去重
        List<PrivateCoachInfo> records = privateCoachInfoDao.queryByCoachIdNative(id);
        Map<Long, Subject> subjectMap = new LinkedHashMap<>();
        Set<Long> memberIds = new HashSet<>();
        for (PrivateCoachInfo r : records) {
            if (r.getSubject() != null) subjectMap.put(r.getSubject().getId(), r.getSubject());
            if (r.getMember() != null) memberIds.add(r.getMember().getId());
        }
        CoachDetailDto vo = new CoachDetailDto();
        vo.setId(coach.getId());
        vo.setRealName(coach.getRealName());
        vo.setSex(coach.getSex());
        vo.setAge(coach.getAge());
        vo.setCoachTeach(coach.getCoachTeach());
        vo.setCoachDate(coach.getCoachDate());
        vo.setCoachStatic(coach.getCoachStatic());
        vo.setMemberCount(memberIds.size());
        List<CoachDetailDto.CourseItem> courses = new ArrayList<>();
        for (Subject s : subjectMap.values()) {
            CoachDetailDto.CourseItem item = new CoachDetailDto.CourseItem();
            item.setCourseId(s.getId());
            item.setCourseName(s.getCourseName());
            item.setSellingPrice(s.getSellingPrice());
            item.setDuration(s.getDuration());
            item.setDescription(s.getDescription());
            courses.add(item);
        }
        vo.setCourses(courses);
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(vo),
                CACHE_EXPIRE, TimeUnit.MINUTES);
        return Result.success(vo);
    }
}
