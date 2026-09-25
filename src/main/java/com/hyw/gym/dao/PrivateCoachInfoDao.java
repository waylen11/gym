package com.hyw.gym.dao;


import com.hyw.gym.entity.PrivateCoachInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 私教信息Dao层接口
 */
public interface PrivateCoachInfoDao extends JpaRepository<PrivateCoachInfo,Long> {

    @Query(value = "select * FROM private_coach_record where member_id =:memberid",nativeQuery = true)
    public List<PrivateCoachInfo> queryByIdNative(@Param("memberid") long memberid);

    @Query(value = "select * FROM private_coach_record where coach_id =:coachid",nativeQuery = true)
    public List<PrivateCoachInfo> queryByCoachIdNative(@Param("coachid") long coachid);

    @Query(value = "select * FROM private_coach_record where course_id =:subjectid",nativeQuery = true)
    public List<PrivateCoachInfo> queryBySubjectIdNative(@Param("subjectid") long subjectid);

}
