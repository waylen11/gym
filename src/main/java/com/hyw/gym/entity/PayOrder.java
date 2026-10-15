package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 模拟支付订单实体（表 pay_order）
 */
@Entity
@Table(name = "pay_order")
@Getter
@Setter
public class PayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_no")
    private String orderNo;          // 订单号（唯一）

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;             // 下单会员

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Membertype membertype;   // 购买的卡类型

    private Double amount;           // 订单金额

    private Integer status;          // 0 待支付 / 1 已支付 / 2 已取消

    @Column(name = "create_time")
    private Timestamp createTime;    // 下单时间

    @Column(name = "pay_time")
    private Timestamp payTime;       // 支付时间
}
