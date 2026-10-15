package com.hyw.gym.dao;
// @author 黄元威


import com.hyw.gym.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 系统公告Dao层接口
 */
public interface MessageDao extends JpaRepository<Messages,Long> {

    @Query(value = "select * FROM system_notice where publisher_id =:userid",nativeQuery = true)
    List<Messages> findMessagesByUserId(@Param("userid") long userid);

    // 会员端公告列表：置顶优先，再按发布时间倒序
    @Query(value = "SELECT * FROM system_notice ORDER BY is_top DESC, notice_id DESC", nativeQuery = true)
    List<Messages> findAllOrdered();
}
