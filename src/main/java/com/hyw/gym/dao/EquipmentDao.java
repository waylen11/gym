package com.hyw.gym.dao;
// @author 黄元威


import com.hyw.gym.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 设备信息Dao层接口
 */
public interface EquipmentDao extends JpaRepository<Equipment,Long> {
}
