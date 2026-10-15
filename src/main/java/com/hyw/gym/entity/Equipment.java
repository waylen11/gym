package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 设备信息实体类
 */
@Entity
@Table(name = "equipment")
@Getter
@Setter
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private long id;
    private String name;
    private String specification;
    private Integer status;
    @Column(name = "purchase_time")
    private java.sql.Date purchaseTime;
}
