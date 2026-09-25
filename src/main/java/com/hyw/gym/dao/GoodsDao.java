package com.hyw.gym.dao;


import com.hyw.gym.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 商品信息Dao层接口
 */
public interface GoodsDao extends JpaRepository<Goods,Long> {
}
