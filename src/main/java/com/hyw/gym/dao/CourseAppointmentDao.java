package com.hyw.gym.dao;
// @author 黄元威

import com.hyw.gym.entity.CourseAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

/**
 * @Description: 课程预约Dao层接口
 */
public interface CourseAppointmentDao extends JpaRepository<CourseAppointment, Long> {

    /**
     * 查询教练名下所有预约(按日期范围)
     */
    @Query("SELECT a FROM CourseAppointment a WHERE a.coach.id = :coachId " +
            "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) " +
            "AND (:endDate IS NULL OR a.appointmentDate <= :endDate) " +
            "ORDER BY a.appointmentDate ASC, a.id ASC")
    List<CourseAppointment> queryByCoach(@Param("coachId") Long coachId,
                                         @Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate);

    /**
     * 检查同一天同一会员同一教练是否已有有效预约(防止重复预约)
     */
    @Query("SELECT a FROM CourseAppointment a WHERE a.member.id = :memberId " +
            "AND a.coach.id = :coachId AND a.appointmentDate = :date AND a.status IN (0,1)")
    List<CourseAppointment> findActiveByMemberCoachDate(@Param("memberId") Long memberId,
                                                        @Param("coachId") Long coachId,
                                                        @Param("date") Date date);

    /**
     * 查询会员名下所有预约
     */
    @Query("SELECT a FROM CourseAppointment a WHERE a.member.id = :memberId ORDER BY a.appointmentDate DESC")
    List<CourseAppointment> queryByMember(@Param("memberId") Long memberId);

    /**
     * 查询所有预约(管理员用)
     */
    @Query("SELECT a FROM CourseAppointment a ORDER BY a.appointmentDate DESC, a.id DESC")
    List<CourseAppointment> queryAll();
}
