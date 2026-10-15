package com.hyw.gym.dao;
// @author 黄元威

import com.hyw.gym.entity.Chongzhi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 充值信息Dao层接口
 */

public interface ChongzhiDao extends JpaRepository<Chongzhi,Long> {

    Page<Chongzhi> findAll(Specification<Chongzhi> specification, Pageable pageable);

    @Query(value = "select * FROM recharge_record where member_id =:memberid ORDER BY recharge_id DESC",nativeQuery = true)
    public List<Chongzhi> queryByIdNative(@Param("memberid") long memberid) ;
}
