package com.hyw.gym.dao;

import com.hyw.gym.entity.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayOrderDao extends JpaRepository<PayOrder, Long> {

    // 我的订单列表（按下单时间倒序）
    @Query(value = "SELECT * FROM pay_order WHERE member_id = :memberId ORDER BY create_time DESC",
            nativeQuery = true)
    List<PayOrder> queryByMemberNative(@Param("memberId") Long memberId);

    // 同一卡类型的待支付订单（防重复下单）
    @Query(value = "SELECT * FROM pay_order WHERE member_id = :memberId AND type_id = :typeId AND status = 0",
            nativeQuery = true)
    List<PayOrder> queryPendingByMemberAndType(@Param("memberId") Long memberId, @Param("typeId") Long typeId);

    // 待支付订单总数（防堆积）
    @Query(value = "SELECT COUNT(*) FROM pay_order WHERE member_id = :memberId AND status = 0",
            nativeQuery = true)
    long countPendingByMember(@Param("memberId") Long memberId);
}
