package com.hyw.gym.dao;


import com.hyw.gym.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 课程信息Dao层接口
 */
public interface SubjectDao extends JpaRepository<Subject,Long> {
}
