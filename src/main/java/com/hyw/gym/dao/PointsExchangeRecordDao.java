package com.hyw.gym.dao;
// @author 黄元威

import com.hyw.gym.entity.PointsExchangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsExchangeRecordDao extends JpaRepository<PointsExchangeRecord, Long> {
    List<PointsExchangeRecord> findByMemberIdOrderByCreateTimeDesc(Long memberId);
}
