package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

/**
 * 我的消费记录 DTO（商品购买明细）
 */
@Data
public class ConsumeRecordDTO {
    private Long id;                // 销售记录 ID
    private GoodsItem goods;        // 商品
    private Integer count;          // 购买数量（实体字段 quantity）
    private Double totalPrice;      // 消费金额（实体字段 amount）
    private String createTime;      // 消费时间（实体字段 saleTime）

    @Data
    public static class GoodsItem {
        private Long id;
        private String goodsName;
        private Double price;
    }
}
