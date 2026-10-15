package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 商品统计信息实体类
 */
@Entity
@Table(name = "goods_sales_record")
@Getter
@Setter
public class GoodInfo implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
    private int quantity;
    private double amount;
    private String remark;
    @Column(name = "sale_time")
    private java.sql.Timestamp saleTime;
}
