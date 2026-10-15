package com.hyw.gym.dao;
// @author 黄元威


import com.hyw.gym.entity.Membertype;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 会员卡类型信息Dao层接口
 */
public interface MemberTypeDao extends JpaRepository<Membertype,Long> {

}
