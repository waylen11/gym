package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 积分兑换记录实体类
 */
@Entity
@Table(name = "points_exchange_record")
@Getter
@Setter
public class PointsExchangeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Column(name = "points_cost")
    private Integer pointsCost;

    @Column(name = "create_time")
    private java.sql.Timestamp createTime;
}
