package com.hyw.gym.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 商品信息实体类
 */
@Entity
@Table(name = "goods")
@Getter
@Setter
public class Goods implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    private long id;
    private String goodsName;
    private String unit;
    private double price;
    private double sellPrice;
    private Integer stock;
    private String description;
}
