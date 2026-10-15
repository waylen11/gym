package com.hyw.gym.dao;
// @author 黄元威


import com.hyw.gym.entity.Loos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 丢失物品信息Dao层接口
 */
public interface LoosDao extends JpaRepository<Loos,Long> {

    /**
     * 失物招领列表（会员端）：status 可选（null=全部），未认领在前、拾取时间倒序
     */
    @Query(value = "SELECT * FROM lost_property WHERE (:status IS NULL OR status = :status) " +
            "ORDER BY status ASC, found_time DESC", nativeQuery = true)
    List<Loos> queryLosts(@Param("status") Integer status);
}
