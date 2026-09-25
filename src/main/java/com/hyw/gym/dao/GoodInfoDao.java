package com.hyw.gym.dao;


import com.hyw.gym.entity.GoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 商品统计信息Dao层接口
 */
public interface GoodInfoDao extends JpaRepository<GoodInfo,Long> {

    @Query(value = "select * FROM goods_sales_record where member_id =:memberid",nativeQuery = true)
    public List<GoodInfo> queryByIdNative(@Param("memberid") long memberid);

    @Query(value = "select * FROM goods_sales_record where goods_id =:goodsid",nativeQuery = true)
    public List<GoodInfo> queryByGoodsIdNative(@Param("goodsid") long goodsid) ;
}
