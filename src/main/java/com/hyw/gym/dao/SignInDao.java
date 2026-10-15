package com.hyw.gym.dao;
// @author 黄元威

import com.hyw.gym.entity.SignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Optional;

public interface SignInDao extends JpaRepository<SignIn, Long> {

    // 查某会员某天是否签过（今天查=防重，昨天查=算连续天数）
    @Query(value = "SELECT * FROM sign_in WHERE member_id = :memberId AND sign_date = :date",
            nativeQuery = true)
    Optional<SignIn> findByMemberIdAndSignDate(@Param("memberId") Long memberId,
                                               @Param("date") Date date);
}
