package com.hyw.gym.dao;
// @author 黄元威


import com.hyw.gym.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 课程信息Dao层接口
 */
public interface SubjectDao extends JpaRepository<Subject,Long> {
}
