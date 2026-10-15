package com.hyw.gym.dto;

import lombok.Data;

/**
 * 商品列表 DTO（会员端）：展示前台在售商品
 */
@Data
public class GoodsDTO {
    private Long goodsId;        // 商品 id
    private String goodsName;    // 商品名称
    private String unit;         // 单位（瓶/根/块…）
    private Double price;        // 原价
    private Double sellPrice;    // 售价
    private Integer stock;       // 库存
    private String stockText;    // 库存状态（有货 / 售罄）
    private String description;  // 商品描述
    private Integer pointsPrice; // 积分兑换所需积分（0=不参与积分兑换）
}
