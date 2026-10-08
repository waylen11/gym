package com.hyw.gym.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.dao.SubjectDao;
import com.hyw.gym.dto.SubjectListDto;
import com.hyw.gym.entity.Subject;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class MemberCourseDaoimpl {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SubjectDao subjectDao;

    private static final String COURSE_LIST_KEY = "course:list";
    private static final long COURSE_LIST_EXPIRE = 30;   // 30 分钟

    public Result<List<SubjectListDto>> showCourse() {
        //查redis缓存
        String cached = stringRedisTemplate.opsForValue().get(COURSE_LIST_KEY);
        if (StrUtil.isNotBlank(cached)){
            List<SubjectListDto> subjectListDtos = JSONUtil.toList(cached, SubjectListDto.class);
            return Result.success(subjectListDtos);
        }
        //未命中,查数据库并写入redis
        List<Subject> subjectList = subjectDao.findAll();
        List<SubjectListDto> subjectListDtos=new ArrayList<>();
        for (Subject s:subjectList){
            SubjectListDto subjectListDto = new SubjectListDto();
            subjectListDto.setCourseName(s.getCourseName());
            subjectListDto.setDuration(s.getDuration()+"min");
            subjectListDto.setSellingPrice(s.getSellingPrice());
            subjectListDtos.add(subjectListDto);
        }
        stringRedisTemplate.opsForValue().set(COURSE_LIST_KEY,JSONUtil.toJsonStr(subjectListDtos),COURSE_LIST_EXPIRE);
        return Result.success(subjectListDtos);
    }

    public Result<Subject> showDetail(Long id) {
        Subject s = subjectDao.findById(id).orElse(null);
        if (s == null) {
            return Result.error(400, "科目不存在");
        }
        return Result.success(s);
    }
}
